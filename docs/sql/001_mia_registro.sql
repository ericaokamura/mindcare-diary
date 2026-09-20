-- PostgreSQL. Execute on the intended development database before running this version.
-- Additive, repeatable migration; no data or credentials are removed.
BEGIN;
ALTER TABLE registro_diario ADD COLUMN IF NOT EXISTS texto_confirmado TEXT;
ALTER TABLE registro_diario ADD COLUMN IF NOT EXISTS origem VARCHAR(20) NOT NULL DEFAULT 'TRADITIONAL';
ALTER TABLE registro_diario ADD COLUMN IF NOT EXISTS id_requisicao UUID;
CREATE UNIQUE INDEX IF NOT EXISTS uk_registro_paciente_requisicao
    ON registro_diario (paciente_id, id_requisicao);
COMMIT;
