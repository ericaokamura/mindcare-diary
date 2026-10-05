package com.fiap.mindcare_diary.security.storage;

import java.sql.*;
import java.io.StringReader;
import java.util.*;

/** Offline migration only: stop all writers and back up database AND key before executing. */
public final class EncryptionMigration {
    public static final Map<String, List<String>> FIELDS = Map.of(
            "registro_diario", List.of("pontos_positivos", "dificuldades_desafios", "texto_confirmado"),
            "relatorio_semanal", List.of("observacoes", "recomendacoes", "relatorio_ia", "resumo"));
    private EncryptionMigration() {}

    private static List<String> legacyNames(String table, String field) {
        return table.equals("relatorio_semanal") && field.equals("relatorio_ia")
                ? List.of(field, "relatorioia") : List.of(field);
    }

    public static void main(String[] args) throws Exception {
        RecordCrypto crypto = new RecordCrypto(required("MINDCARE_DATA_KEY"));
        try (Connection c = DriverManager.getConnection(required("SPRING_DATASOURCE_URL"),
                required("SPRING_DATASOURCE_USERNAME"), required("DB_PASSWORD"))) {
            migrate(c, crypto, "true".equalsIgnoreCase(System.getenv("MINDCARE_PURGE_LEGACY_VECTORS")));
            System.out.println("Migração criptográfica concluída e verificada. Nenhum texto ou chave foi impresso.");
        }
    }

    private static String required(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) throw new IllegalStateException("Configure " + name);
        return value;
    }

    public static boolean tableExists(Connection c, String table) throws SQLException {
        String schema = c.getSchema();
        if (schema == null) schema = c.getMetaData().getUserName();
        try (ResultSet rs = c.getMetaData().getTables(null, schema, table.toUpperCase(Locale.ROOT), new String[]{"TABLE"})) {
            return rs.next();
        }
    }

    private static Set<String> columns(Connection c, String table) throws SQLException {
        Set<String> result = new HashSet<>();
        try (Statement s = c.createStatement(); ResultSet rs = s.executeQuery("SELECT * FROM " + table + " WHERE 1=0")) {
            for (int i=1; i<=rs.getMetaData().getColumnCount(); i++) result.add(rs.getMetaData().getColumnName(i).toLowerCase(Locale.ROOT));
        }
        return result;
    }

    public static void verify(Connection c, RecordCrypto crypto) throws SQLException {
        for (var entry : FIELDS.entrySet()) {
            String table = entry.getKey();
            Set<String> columns = columns(c, table);
            for (String field : entry.getValue()) {
                if (!columns.contains(field + "_enc")) throw new IllegalStateException("Execute a migração de criptografia antes de iniciar.");
                for (String legacy : legacyNames(table, field)) if (columns.contains(legacy)) {
                    try (Statement s = c.createStatement(); ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM " + table + " WHERE " + legacy + " IS NOT NULL")) {
                        rs.next();
                        if (rs.getLong(1) > 0) throw new IllegalStateException("Há registros legados: execute a migração offline de criptografia.");
                    }
                }
                try (Statement s = c.createStatement(); ResultSet rs = s.executeQuery("SELECT " + field + "_enc FROM " + table + " WHERE " + field + "_enc IS NOT NULL")) {
                    while (rs.next()) crypto.decrypt(rs.getString(1), table + "." + field);
                }
            }
        }
        if (tableExists(c, "spring_ai_vectors")) {
            try (Statement s = c.createStatement(); ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM spring_ai_vectors")) {
                rs.next();
                if (rs.getLong(1) > 0) throw new IllegalStateException("Cache vetorial legado presente: revise e execute a migração offline.");
            }
        }
    }

    public static void migrate(Connection c, RecordCrypto crypto, boolean purgeLegacyVectors) throws SQLException {
        // Oracle DDL commits implicitly. Only additive schema changes precede the data transaction.
        for (var entry : FIELDS.entrySet()) {
            Set<String> columns = columns(c, entry.getKey());
            for (String field : entry.getValue()) if (!columns.contains(field + "_enc")) {
                try (Statement s = c.createStatement()) { s.executeUpdate("ALTER TABLE " + entry.getKey() + " ADD " + field + "_enc CLOB"); }
            }
        }
        boolean originalAutoCommit = c.getAutoCommit();
        c.setAutoCommit(false);
        try {
            if (c.getMetaData().getDatabaseProductName().equals("Oracle")) {
                for (String table : FIELDS.keySet()) try (Statement s = c.createStatement()) {
                    s.executeUpdate("LOCK TABLE " + table + " IN EXCLUSIVE MODE NOWAIT");
                }
            }
            for (var entry : FIELDS.entrySet()) {
                String table = entry.getKey();
                Set<String> columns = columns(c, table);
                for (String field : entry.getValue()) {
                  for (String legacy : legacyNames(table, field)) {
                    if (!columns.contains(legacy)) continue;
                    try (Statement s = c.createStatement();
                         ResultSet rs = s.executeQuery("SELECT id, " + legacy + ", " + field + "_enc FROM " + table + " WHERE " + legacy + " IS NOT NULL");
                         PreparedStatement update = c.prepareStatement("UPDATE " + table + " SET " + field + "_enc = ?, " + legacy + " = NULL WHERE id = ?")) {
                        while (rs.next()) {
                            String plain = rs.getString(2);
                            String encrypted = rs.getString(3);
                            if (encrypted == null) encrypted = crypto.encrypt(plain, table + "." + field);
                            if (!plain.equals(crypto.decrypt(encrypted, table + "." + field))) throw new IllegalStateException("Conflito na migração; nenhuma alteração de dados foi confirmada.");
                            update.setCharacterStream(1, new StringReader(encrypted), encrypted.length());
                            update.setLong(2, rs.getLong(1)); update.executeUpdate();
                        }
                    }
                  }
                }
            }
            if (purgeLegacyVectors && tableExists(c, "spring_ai_vectors")) {
                try (Statement s = c.createStatement()) { s.executeUpdate("DELETE FROM spring_ai_vectors"); }
            }
            verify(c, crypto);
            c.commit();
        } catch (SQLException | RuntimeException ex) { c.rollback(); throw ex; }
        finally { c.setAutoCommit(originalAutoCommit); }
    }
}
