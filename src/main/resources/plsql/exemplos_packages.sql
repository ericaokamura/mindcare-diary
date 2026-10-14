-- =============================================================================
-- Demonstração dos packages do MindCare.
-- Cria dados próprios (clínica, profissional e paciente "demo"), exercita as
-- rotinas e termina com ROLLBACK: nada fica gravado no banco.
-- Pré-requisito: instalar_packages.sql executado no mesmo schema.
-- =============================================================================

SET SERVEROUTPUT ON

DECLARE
    V_CLINICA_ID        CLINICA.ID%TYPE;
    V_PACIENTE_ID       USUARIO.ID%TYPE;
    V_PROFISSIONAL_ID   USUARIO.ID%TYPE;
    V_ESTADO            USUARIO.ESTADO_PACIENTE%TYPE;
    V_NUMERO            CONSULTA.NUMERO%TYPE;
    V_DIA               DATE := PKG_MINDCARE_COMUM.FN_PROXIMO_DIA_UTIL(SYSDATE + 1);
    V_ANO               PLS_INTEGER := EXTRACT(YEAR FROM SYSDATE);
    V_MES               PLS_INTEGER := EXTRACT(MONTH FROM SYSDATE);
    V_CURSOR            SYS_REFCURSOR;
    V_DATA_HORA         TIMESTAMP;
    V_TIPO              USUARIO.TIPO_PROFISSIONAL%TYPE;
    V_NIVEL             REGISTRO_DIARIO.NIVEL_HUMOR%TYPE;
    V_SCORE             NUMBER;
    V_QTD               NUMBER;
    V_PACIENTES         PLS_INTEGER;
    V_PROFISSIONAIS     PLS_INTEGER;
    V_ATENCAO           PLS_INTEGER;
    V_AGENDADAS         PLS_INTEGER;
    V_ATENDIDAS         PLS_INTEGER;
    V_CANCELADAS        PLS_INTEGER;
    V_TAXA_CANCEL       NUMBER;
    V_FATURAMENTO       NUMBER;
    V_RECEITA           NUMBER;

    PROCEDURE IMPRIMIR (P_TEXTO IN VARCHAR2) IS
    BEGIN
        DBMS_OUTPUT.PUT_LINE(P_TEXTO);
    END IMPRIMIR;
BEGIN
    -- Dados de demonstração ----------------------------------------------------
    INSERT INTO CLINICA (NOME, CNPJ, ENDERECO, TAXA_COMISSAO, PLANO_ASSINATURA)
    VALUES ('Clínica Demo PL/SQL', '00.000.000/0001-91', 'Rua Demo, 100', 0.10, 'CLINICA')
    RETURNING ID INTO V_CLINICA_ID;

    INSERT INTO USUARIO (DTYPE, NOME_USUARIO, NOME_COMPLETO, ATIVO, BLOQUEADO, USER_ROLE, TIPO_PROFISSIONAL, CLINICA_ID)
    VALUES ('Profissional', 'demo.profissional', 'Profissional Demo', 1, 0, 'PROFISSIONAL', 'PSICOLOGO', V_CLINICA_ID)
    RETURNING ID INTO V_PROFISSIONAL_ID;

    INSERT INTO USUARIO (DTYPE, NOME_USUARIO, NOME_COMPLETO, ATIVO, BLOQUEADO, USER_ROLE, ESTADO_PACIENTE, CLINICA_ID)
    VALUES ('Paciente', 'demo.paciente', 'Paciente Demo', 1, 0, 'PACIENTE', 'ESTAVEL', V_CLINICA_ID)
    RETURNING ID INTO V_PACIENTE_ID;

    -- PKG_MINDCARE_DIARIO -----------------------------------------------------
    IMPRIMIR('== PKG_MINDCARE_DIARIO');
    -- Semana anterior boa, semana atual ruim
    INSERT INTO REGISTRO_DIARIO (PACIENTE_ID, NIVEL_HUMOR, DATA_HORA_CRIACAO, ORIGEM, ID_REQUISICAO)
    SELECT V_PACIENTE_ID, H.NIVEL, LOCALTIMESTAMP - NUMTODSINTERVAL(H.DIAS_ATRAS, 'DAY'), 'TRADITIONAL', RAWTOHEX(SYS_GUID())
      FROM (SELECT 'BOM' AS NIVEL, 10 AS DIAS_ATRAS FROM DUAL UNION ALL
            SELECT 'OTIMO', 9 FROM DUAL UNION ALL
            SELECT 'NEUTRO', 3 FROM DUAL UNION ALL
            SELECT 'MAL', 2 FROM DUAL UNION ALL
            SELECT 'PESSIMO', 1 FROM DUAL) H;

    PKG_MINDCARE_DIARIO.REGISTRA_ALERTA_NIVEL_HUMOR_PACIENTE('demo.paciente');
    SELECT ESTADO_PACIENTE INTO V_ESTADO FROM USUARIO WHERE ID = V_PACIENTE_ID;
    IMPRIMIR('Humor médio (7 dias): '
        || TO_CHAR(PKG_MINDCARE_DIARIO.FN_HUMOR_MEDIO(V_PACIENTE_ID, LOCALTIMESTAMP - INTERVAL '7' DAY, LOCALTIMESTAMP), 'FM0.00')
        || ' -> estado: ' || V_ESTADO);

    V_CURSOR := PKG_MINDCARE_DIARIO.FN_DISTRIBUICAO_HUMOR('demo.paciente', 30);
    LOOP
        FETCH V_CURSOR INTO V_NIVEL, V_SCORE, V_QTD;
        EXIT WHEN V_CURSOR%NOTFOUND;
        IMPRIMIR('  ' || RPAD(V_NIVEL, 8) || ' score ' || V_SCORE || ': ' || V_QTD);
    END LOOP;
    CLOSE V_CURSOR;

    -- PKG_MINDCARE_AGENDA -----------------------------------------------------
    IMPRIMIR('== PKG_MINDCARE_AGENDA');
    PKG_MINDCARE_AGENDA.PRC_AGENDAR_CONSULTA(
        P_PACIENTE_NOME_USUARIO     => 'demo.paciente',
        P_PROFISSIONAL_NOME_USUARIO => 'demo.profissional',
        P_DATA_HORA                 => CAST(V_DIA AS TIMESTAMP) + INTERVAL '10' HOUR,
        P_VALOR                     => 200,
        P_MODALIDADE                => PKG_MINDCARE_AGENDA.C_TELECONSULTA,
        P_NUMERO                    => V_NUMERO);
    IMPRIMIR('Consulta agendada: ' || V_NUMERO || ' em ' || TO_CHAR(V_DIA, 'DD/MM/YYYY') || ' 10:00');

    BEGIN
        PKG_MINDCARE_AGENDA.PRC_AGENDAR_CONSULTA('demo.paciente', 'demo.profissional',
            CAST(V_DIA AS TIMESTAMP) + INTERVAL '10' HOUR, 200, PKG_MINDCARE_AGENDA.C_PRESENCIAL, V_NUMERO);
    EXCEPTION
        WHEN OTHERS THEN IMPRIMIR('Conflito bloqueado: ' || SQLERRM);
    END;

    IMPRIMIR('Validação de sábado: ' || PKG_MINDCARE_AGENDA.FN_VALIDAR_HORARIO(
        CAST(TRUNC(SYSDATE, 'IW') + 5 AS TIMESTAMP) + INTERVAL '7' DAY + INTERVAL '10' HOUR));

    IMPRIMIR('Horários livres em ' || TO_CHAR(V_DIA, 'DD/MM/YYYY') || ':');
    V_CURSOR := PKG_MINDCARE_AGENDA.FN_HORARIOS_DISPONIVEIS('demo.profissional', V_DIA);
    LOOP
        FETCH V_CURSOR INTO V_DATA_HORA, V_TIPO;
        EXIT WHEN V_CURSOR%NOTFOUND;
        IMPRIMIR('  ' || TO_CHAR(V_DATA_HORA, 'HH24:MI') || ' (' || V_TIPO || ')');
    END LOOP;
    CLOSE V_CURSOR;

    -- Consulta que acabou de acontecer, para gerar faturamento no mês
    INSERT INTO CONSULTA (NUMERO, PROFISSIONAL_ID, PACIENTE_ID, CLINICA_ID, VALOR_CONSULTA,
                          CONSULTA_MODALIDADE, DATA_HORA_CONSULTA, ATENDIDA, CANCELADA)
    VALUES ('DEMO01', V_PROFISSIONAL_ID, V_PACIENTE_ID, V_CLINICA_ID, 250,
            'PRESENCIAL', LOCALTIMESTAMP - INTERVAL '1' MINUTE, 0, 0);
    PKG_MINDCARE_AGENDA.PRC_REGISTRAR_ATENDIMENTO('DEMO01');
    PKG_MINDCARE_AGENDA.PRC_CANCELAR_CONSULTA(V_NUMERO);
    IMPRIMIR('Consulta DEMO01 atendida; consulta ' || V_NUMERO || ' cancelada.');
    IMPRIMIR('Taxa de comparecimento do paciente: ' || PKG_MINDCARE_AGENDA.FN_TAXA_COMPARECIMENTO('demo.paciente') || '%');

    -- PKG_MINDCARE_CLINICA ----------------------------------------------------
    IMPRIMIR('== PKG_MINDCARE_CLINICA');
    IMPRIMIR('Faturamento (por id):          ' || PKG_MINDCARE_CLINICA.FN_FATURAMENTO_MES(V_CLINICA_ID, V_ANO, V_MES));
    IMPRIMIR('Receita líquida (por CNPJ):    ' || PKG_MINDCARE_CLINICA.FN_RECEITA_LIQUIDA_MES('00.000.000/0001-91', V_ANO, V_MES));

    PKG_MINDCARE_CLINICA.PRC_RESUMO_DASHBOARD(V_CLINICA_ID, V_ANO, V_MES,
        V_PACIENTES, V_PROFISSIONAIS, V_ATENCAO, V_AGENDADAS, V_ATENDIDAS, V_CANCELADAS,
        V_TAXA_CANCEL, V_FATURAMENTO, V_RECEITA);
    IMPRIMIR('Dashboard: ' || V_PACIENTES || ' paciente(s), ' || V_PROFISSIONAIS || ' profissional(is), '
        || V_ATENCAO || ' em atenção; consultas: ' || V_AGENDADAS || ' agendada(s), '
        || V_ATENDIDAS || ' atendida(s), ' || V_CANCELADAS || ' cancelada(s) ('
        || V_TAXA_CANCEL || '%); faturamento ' || V_FATURAMENTO || ', receita líquida ' || V_RECEITA);

    IMPRIMIR('Agenda da clínica (hoje + 7 dias):');
    V_CURSOR := PKG_MINDCARE_CLINICA.FN_AGENDA_CLINICA(V_CLINICA_ID, SYSDATE, SYSDATE + 7);
    DECLARE
        V_C_NUMERO      CONSULTA.NUMERO%TYPE;
        V_C_DATA        CONSULTA.DATA_HORA_CONSULTA%TYPE;
        V_C_MODALIDADE  CONSULTA.CONSULTA_MODALIDADE%TYPE;
        V_C_VALOR       CONSULTA.VALOR_CONSULTA%TYPE;
        V_C_ATENDIDA    CONSULTA.ATENDIDA%TYPE;
        V_C_CANCELADA   CONSULTA.CANCELADA%TYPE;
        V_C_PACIENTE    USUARIO.NOME_COMPLETO%TYPE;
        V_C_PROF        USUARIO.NOME_COMPLETO%TYPE;
        V_C_TIPO        USUARIO.TIPO_PROFISSIONAL%TYPE;
    BEGIN
        LOOP
            FETCH V_CURSOR INTO V_C_NUMERO, V_C_DATA, V_C_MODALIDADE, V_C_VALOR, V_C_ATENDIDA,
                                V_C_CANCELADA, V_C_PACIENTE, V_C_PROF, V_C_TIPO;
            EXIT WHEN V_CURSOR%NOTFOUND;
            IMPRIMIR('  ' || TO_CHAR(V_C_DATA, 'DD/MM HH24:MI') || ' ' || V_C_NUMERO || ' ' || V_C_PACIENTE
                || ' com ' || V_C_PROF || CASE WHEN V_C_CANCELADA = 1 THEN ' [cancelada]'
                                               WHEN V_C_ATENDIDA = 1 THEN ' [atendida]' END);
        END LOOP;
        CLOSE V_CURSOR;
    END;
END;
/

ROLLBACK;
