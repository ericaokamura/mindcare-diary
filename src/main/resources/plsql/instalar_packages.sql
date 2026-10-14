-- =============================================================================
-- Instala os packages do MindCare na ordem de dependência.
-- Executar conectado ao schema MINDCARE (ou MINDCARE_TEST), a partir desta pasta:
--   SQLcl / SQL*Plus:  @instalar_packages.sql
--   SQL Developer:     abrir este arquivo e executar como script (F5)
-- =============================================================================

@@pkg_mindcare_comum.sql
@@pkg_mindcare_diario.sql
@@pkg_mindcare_agenda.sql
@@pkg_mindcare_clinica.sql

-- A function avulsa CARREGA_ULTIMOS_REGISTROS_DIARIOS foi incorporada ao
-- PKG_MINDCARE_DIARIO; remove a versão antiga se ela existir (ORA-04043 = não existe).
BEGIN
    EXECUTE IMMEDIATE 'DROP FUNCTION CARREGA_ULTIMOS_REGISTROS_DIARIOS';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE <> -4043 THEN
            RAISE;
        END IF;
END;
/

-- Conferência: todos os objetos devem aparecer como VALID e sem erros listados
SELECT OBJECT_NAME, OBJECT_TYPE, STATUS
  FROM USER_OBJECTS
 WHERE OBJECT_NAME LIKE 'PKG_MINDCARE%'
 ORDER BY OBJECT_NAME, OBJECT_TYPE;

SELECT NAME, TYPE, LINE, POSITION, TEXT
  FROM USER_ERRORS
 WHERE NAME LIKE 'PKG_MINDCARE%'
 ORDER BY NAME, TYPE, SEQUENCE;
