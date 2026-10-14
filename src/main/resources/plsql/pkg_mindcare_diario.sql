-- =============================================================================
-- PKG_MINDCARE_DIARIO
-- Acompanhamento emocional do paciente a partir do diário.
-- Os textos do diário são cifrados pela aplicação (colunas *_ENC); por isso as
-- regras deste package usam apenas os dados em claro: NIVEL_HUMOR e datas.
-- Depende de: PKG_MINDCARE_COMUM.
-- =============================================================================

CREATE OR REPLACE PACKAGE PKG_MINDCARE_DIARIO AS

    C_DIAS_JANELA      CONSTANT PLS_INTEGER := 7;    -- janela de análise (dias)
    C_LIMITE_ATENCAO   CONSTANT NUMBER      := 2.5;  -- média abaixo disso => ATENCAO
    C_DELTA_MELHORA    CONSTANT NUMBER      := 0.5;  -- ganho mínimo sobre a janela anterior => MELHORANDO

    -- Registros dos últimos 7 dias do paciente, mais recentes primeiro.
    -- Usada pelo backend em RegistroDiarioRepository.carregarUltimosRegistrosDiarios
    -- (antes era a function avulsa CARREGA_ULTIMOS_REGISTROS_DIARIOS).
    FUNCTION CARREGA_ULTIMOS_REGISTROS_DIARIOS (
        PACIENTE_NOME_USUARIO IN USUARIO.NOME_USUARIO%TYPE
    ) RETURN SYS_REFCURSOR;

    -- Converte NIVEL_HUMOR em escala de bem-estar: 1 (PESSIMO) .. 5 (OTIMO).
    -- SEM_DEFINICAO ou valor desconhecido retornam NULL (ignorados nas médias).
    FUNCTION FN_SCORE_HUMOR (
        P_NIVEL_HUMOR IN REGISTRO_DIARIO.NIVEL_HUMOR%TYPE
    ) RETURN NUMBER DETERMINISTIC;

    -- Média de bem-estar do paciente no intervalo [P_INICIO, P_FIM); NULL se não houver registros.
    FUNCTION FN_HUMOR_MEDIO (
        P_PACIENTE_ID IN USUARIO.ID%TYPE,
        P_INICIO      IN TIMESTAMP,
        P_FIM         IN TIMESTAMP
    ) RETURN NUMBER;

    -- Classifica o paciente comparando a última janela com a anterior:
    -- ATENCAO (média baixa), MELHORANDO (subiu C_DELTA_MELHORA ou mais) ou ESTAVEL.
    -- Retorna NULL quando não há registros na última janela.
    FUNCTION FN_CALCULAR_ESTADO_PACIENTE (
        P_PACIENTE_ID IN USUARIO.ID%TYPE
    ) RETURN USUARIO.ESTADO_PACIENTE%TYPE;

    -- Recalcula e grava USUARIO.ESTADO_PACIENTE.
    -- Chamada pelo backend em AlertaNivelHumorPacienteRepository.
    PROCEDURE REGISTRA_ALERTA_NIVEL_HUMOR_PACIENTE (
        P_PACIENTE_NOME_USUARIO IN USUARIO.NOME_USUARIO%TYPE
    );

    -- Quantidade de registros por nível de humor nos últimos P_DIAS dias
    -- (colunas: NIVEL_HUMOR, SCORE, QUANTIDADE).
    FUNCTION FN_DISTRIBUICAO_HUMOR (
        P_PACIENTE_NOME_USUARIO IN USUARIO.NOME_USUARIO%TYPE,
        P_DIAS                  IN PLS_INTEGER DEFAULT 30
    ) RETURN SYS_REFCURSOR;

END PKG_MINDCARE_DIARIO;
/

CREATE OR REPLACE PACKAGE BODY PKG_MINDCARE_DIARIO AS

    FUNCTION CARREGA_ULTIMOS_REGISTROS_DIARIOS (
        PACIENTE_NOME_USUARIO IN USUARIO.NOME_USUARIO%TYPE
    ) RETURN SYS_REFCURSOR
    IS
        V_PACIENTE_ID USUARIO.ID%TYPE;
        V_RESULTADO   SYS_REFCURSOR;
    BEGIN
        V_PACIENTE_ID := PKG_MINDCARE_COMUM.FN_ID_PACIENTE(PACIENTE_NOME_USUARIO);

        OPEN V_RESULTADO FOR
            SELECT *
              FROM REGISTRO_DIARIO
             WHERE PACIENTE_ID = V_PACIENTE_ID
               AND DATA_HORA_CRIACAO >= SYSTIMESTAMP - NUMTODSINTERVAL(C_DIAS_JANELA, 'DAY')
             ORDER BY DATA_HORA_CRIACAO DESC;

        RETURN V_RESULTADO;
    END CARREGA_ULTIMOS_REGISTROS_DIARIOS;

    FUNCTION FN_SCORE_HUMOR (
        P_NIVEL_HUMOR IN REGISTRO_DIARIO.NIVEL_HUMOR%TYPE
    ) RETURN NUMBER DETERMINISTIC
    IS
    BEGIN
        RETURN CASE P_NIVEL_HUMOR
                   WHEN 'OTIMO'   THEN 5
                   WHEN 'BOM'     THEN 4
                   WHEN 'NEUTRO'  THEN 3
                   WHEN 'MAL'     THEN 2
                   WHEN 'PESSIMO' THEN 1
               END;
    END FN_SCORE_HUMOR;

    FUNCTION FN_HUMOR_MEDIO (
        P_PACIENTE_ID IN USUARIO.ID%TYPE,
        P_INICIO      IN TIMESTAMP,
        P_FIM         IN TIMESTAMP
    ) RETURN NUMBER
    IS
        V_MEDIA NUMBER;
    BEGIN
        SELECT AVG(PKG_MINDCARE_DIARIO.FN_SCORE_HUMOR(R.NIVEL_HUMOR))
          INTO V_MEDIA
          FROM REGISTRO_DIARIO R
         WHERE R.PACIENTE_ID = P_PACIENTE_ID
           AND R.DATA_HORA_CRIACAO >= P_INICIO
           AND R.DATA_HORA_CRIACAO <  P_FIM;
        RETURN V_MEDIA;
    END FN_HUMOR_MEDIO;

    FUNCTION FN_CALCULAR_ESTADO_PACIENTE (
        P_PACIENTE_ID IN USUARIO.ID%TYPE
    ) RETURN USUARIO.ESTADO_PACIENTE%TYPE
    IS
        V_AGORA        TIMESTAMP := LOCALTIMESTAMP;
        V_JANELA       INTERVAL DAY TO SECOND := NUMTODSINTERVAL(C_DIAS_JANELA, 'DAY');
        V_MEDIA_ATUAL  NUMBER;
        V_MEDIA_ANTES  NUMBER;
    BEGIN
        -- Limite superior um segundo à frente para incluir registros gravados neste instante
        V_MEDIA_ATUAL := FN_HUMOR_MEDIO(P_PACIENTE_ID, V_AGORA - V_JANELA, V_AGORA + INTERVAL '1' SECOND);
        IF V_MEDIA_ATUAL IS NULL THEN
            RETURN NULL;
        END IF;

        IF V_MEDIA_ATUAL < C_LIMITE_ATENCAO THEN
            RETURN 'ATENCAO';
        END IF;

        V_MEDIA_ANTES := FN_HUMOR_MEDIO(P_PACIENTE_ID, V_AGORA - 2 * V_JANELA, V_AGORA - V_JANELA);
        IF V_MEDIA_ANTES IS NOT NULL AND V_MEDIA_ATUAL - V_MEDIA_ANTES >= C_DELTA_MELHORA THEN
            RETURN 'MELHORANDO';
        END IF;

        RETURN 'ESTAVEL';
    END FN_CALCULAR_ESTADO_PACIENTE;

    PROCEDURE REGISTRA_ALERTA_NIVEL_HUMOR_PACIENTE (
        P_PACIENTE_NOME_USUARIO IN USUARIO.NOME_USUARIO%TYPE
    )
    IS
        V_PACIENTE_ID USUARIO.ID%TYPE;
        V_ESTADO      USUARIO.ESTADO_PACIENTE%TYPE;
    BEGIN
        V_PACIENTE_ID := PKG_MINDCARE_COMUM.FN_ID_PACIENTE(P_PACIENTE_NOME_USUARIO);
        V_ESTADO := FN_CALCULAR_ESTADO_PACIENTE(V_PACIENTE_ID);

        -- Sem registros recentes não há base para mudar o estado atual
        IF V_ESTADO IS NULL THEN
            RETURN;
        END IF;

        UPDATE USUARIO
           SET ESTADO_PACIENTE = V_ESTADO
         WHERE ID = V_PACIENTE_ID
           AND (ESTADO_PACIENTE IS NULL OR ESTADO_PACIENTE <> V_ESTADO);
    END REGISTRA_ALERTA_NIVEL_HUMOR_PACIENTE;

    FUNCTION FN_DISTRIBUICAO_HUMOR (
        P_PACIENTE_NOME_USUARIO IN USUARIO.NOME_USUARIO%TYPE,
        P_DIAS                  IN PLS_INTEGER DEFAULT 30
    ) RETURN SYS_REFCURSOR
    IS
        V_PACIENTE_ID USUARIO.ID%TYPE;
        V_RESULTADO   SYS_REFCURSOR;
    BEGIN
        IF P_DIAS IS NULL OR P_DIAS < 1 THEN
            RAISE_APPLICATION_ERROR(PKG_MINDCARE_COMUM.C_ERR_PARAMETRO_INVALIDO, 'Quantidade de dias inválida.');
        END IF;
        V_PACIENTE_ID := PKG_MINDCARE_COMUM.FN_ID_PACIENTE(P_PACIENTE_NOME_USUARIO);

        OPEN V_RESULTADO FOR
            SELECT R.NIVEL_HUMOR,
                   PKG_MINDCARE_DIARIO.FN_SCORE_HUMOR(R.NIVEL_HUMOR) AS SCORE,
                   COUNT(*) AS QUANTIDADE
              FROM REGISTRO_DIARIO R
             WHERE R.PACIENTE_ID = V_PACIENTE_ID
               AND R.DATA_HORA_CRIACAO >= LOCALTIMESTAMP - NUMTODSINTERVAL(P_DIAS, 'DAY')
             GROUP BY R.NIVEL_HUMOR
             ORDER BY SCORE DESC NULLS LAST;

        RETURN V_RESULTADO;
    END FN_DISTRIBUICAO_HUMOR;

END PKG_MINDCARE_DIARIO;
/
