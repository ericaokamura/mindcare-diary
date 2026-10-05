package com.fiap.mindcare_diary.services;

import com.fiap.mindcare_diary.security.storage.*;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import java.util.Base64;
import static org.junit.jupiter.api.Assertions.*;

class RecordCryptoTest {
    private final RecordCrypto crypto = new RecordCrypto("AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8=");

    @Test void roundTripRandomNonceUnicodeLongNullAndEmpty() {
        String text = "Relato privado: emoção 😀 ".repeat(3000);
        String a = crypto.encrypt(text, "field"), b = crypto.encrypt(text, "field");
        assertNotEquals(a, b); assertFalse(a.contains("Relato"));
        assertEquals(text, crypto.decrypt(a,"field"));
        assertEquals("", crypto.decrypt(crypto.encrypt("","field"),"field"));
        assertNull(crypto.encrypt(null,"field")); assertNull(crypto.decrypt(null,"field"));
    }
    @Test void rejectsMissingKeyWrongKeyPlaintextTamperingAndFieldSwap() {
        assertThrows(IllegalStateException.class, () -> new RecordCrypto(""));
        assertThrows(IllegalStateException.class, () -> new RecordCrypto("not-base64"));
        String encrypted = crypto.encrypt("segredo", "a");
        var other = new RecordCrypto(Base64.getEncoder().encodeToString(new byte[32]));
        assertThrows(IllegalStateException.class, () -> other.decrypt(encrypted,"a"));
        assertThrows(IllegalStateException.class, () -> crypto.decrypt(encrypted,"b"));
        assertThrows(IllegalStateException.class, () -> crypto.decrypt("segredo","a"));
        byte[] bytes = Base64.getDecoder().decode(encrypted.substring(4)); bytes[15] ^= 1;
        assertThrows(IllegalStateException.class, () -> crypto.decrypt("mc1:" + Base64.getEncoder().encodeToString(bytes),"a"));
    }
    @Test void migratesLegacyDataClearsPlaintextAndIsRepeatable() throws Exception {
        try (var c = database()) {
            EncryptionMigration.migrate(c,crypto,false);
            EncryptionMigration.verify(c,crypto);
            String before;
            try (var s=c.createStatement(); var rs=s.executeQuery("SELECT pontos_positivos,pontos_positivos_enc FROM registro_diario")) {
                rs.next(); assertNull(rs.getString(1)); before=rs.getString(2);
                assertEquals("legado",crypto.decrypt(before,"registro_diario.pontos_positivos"));
            }
            EncryptionMigration.migrate(c,crypto,false);
            try (var s=c.createStatement(); var rs=s.executeQuery("SELECT pontos_positivos_enc FROM registro_diario")) {
                rs.next(); assertEquals(before, rs.getString(1));
            }
        }
    }
    @Test void refusesLegacyVectorCacheAndRollsBackUntilExplicitPurge() throws Exception {
        try (var c = database()) {
            try(var s=c.createStatement()) { s.execute("CREATE TABLE spring_ai_vectors(content CLOB)"); s.execute("INSERT INTO spring_ai_vectors VALUES('copia antiga')"); }
            assertThrows(IllegalStateException.class, () -> EncryptionMigration.migrate(c,crypto,false));
            try(var s=c.createStatement(); var rs=s.executeQuery("SELECT pontos_positivos FROM registro_diario")) { rs.next(); assertEquals("legado",rs.getString(1)); }
            EncryptionMigration.migrate(c,crypto,true);
            EncryptionMigration.verify(c,crypto);
        }
    }
    @Test void migratesOldReportColumnAndRejectsConflictingCopies() throws Exception {
        try (var c=database(); var s=c.createStatement()) {
            s.execute("ALTER TABLE relatorio_semanal ADD relatorioia CLOB");
            s.execute("INSERT INTO relatorio_semanal(id,relatorioia) VALUES(1,'Resumo antigo')");
            EncryptionMigration.migrate(c,crypto,false);
            try(var rs=s.executeQuery("SELECT relatorioia,relatorio_ia_enc FROM relatorio_semanal")) {
                rs.next(); assertNull(rs.getString(1)); assertEquals("Resumo antigo",crypto.decrypt(rs.getString(2),"relatorio_semanal.relatorio_ia"));
            }
            s.execute("UPDATE relatorio_semanal SET relatorioia='outro conteúdo'");
            assertThrows(IllegalStateException.class, () -> EncryptionMigration.verify(c,crypto));
            assertThrows(IllegalStateException.class, () -> EncryptionMigration.migrate(c,crypto,false));
            try(var rs=s.executeQuery("SELECT relatorioia FROM relatorio_semanal")) { rs.next(); assertEquals("outro conteúdo",rs.getString(1)); }
        }
    }
    private java.sql.Connection database() throws Exception {
        var c = new DriverManagerDataSource("jdbc:h2:mem:"+java.util.UUID.randomUUID()+";MODE=Oracle", "sa", "").getConnection();
        try (var s=c.createStatement()) {
            s.execute("CREATE TABLE registro_diario(id BIGINT, pontos_positivos CLOB, dificuldades_desafios CLOB, texto_confirmado CLOB)");
            s.execute("CREATE TABLE relatorio_semanal(id BIGINT, observacoes CLOB, recomendacoes CLOB, relatorio_ia CLOB, resumo CLOB)");
            s.execute("INSERT INTO registro_diario VALUES(1,'legado',NULL,NULL)");
        }
        return c;
    }
}
