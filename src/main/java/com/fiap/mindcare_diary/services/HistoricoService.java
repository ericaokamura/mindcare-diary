package com.fiap.mindcare_diary.services;

import com.fiap.mindcare_diary.models.enums.NivelHumor;
import com.fiap.mindcare_diary.models.enums.OrigemRegistro;
import com.fiap.mindcare_diary.security.storage.RecordCrypto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.*;

/** Metadata is filtered by Oracle; textual search only sees decrypted patient-scoped rows in memory. */
@Service
public class HistoricoService {
    private final JdbcTemplate jdbc;
    private final RecordCrypto crypto;
    public HistoricoService(JdbcTemplate jdbc, RecordCrypto crypto) { this.jdbc = jdbc; this.crypto = crypto; }
    public record Registro(Long id, String nivelHumor, String pontosPositivos, String dificuldadesDesafios,
                           String textoConfirmado, String origem, String dataHoraCriacao) {}
    public record Pagina(List<Registro> registros, int pagina, boolean temMais) {}

    public Pagina buscar(Long pacienteId, String texto, LocalDate inicio, LocalDate fim,
                         NivelHumor humor, OrigemRegistro origem, int pagina, int tamanho) {
        if (pacienteId == null || pagina < 0 || pagina > 100000 || tamanho < 1 || tamanho > 50 ||
                (texto != null && texto.length() > 200) || (inicio != null && fim != null && inicio.isAfter(fim)) ||
                (fim != null && fim.getYear() > 9998))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Filtros inválidos.");
        var where = new StringBuilder(" WHERE paciente_id = ?");
        List<Object> args = new ArrayList<>(); args.add(pacienteId);
        if (inicio != null) { where.append(" AND data_hora_criacao >= ?"); args.add(java.sql.Timestamp.valueOf(inicio.atStartOfDay())); }
        if (fim != null) { where.append(" AND data_hora_criacao < ?"); args.add(java.sql.Timestamp.valueOf(fim.plusDays(1).atStartOfDay())); }
        if (humor != null) { where.append(" AND nivel_humor = ?"); args.add(humor.name()); }
        if (origem != null) { where.append(" AND origem = ?"); args.add(origem.name()); }
        String search = texto == null ? "" : texto.trim().toLowerCase(Locale.ROOT);
        String sql = "SELECT id, nivel_humor, pontos_positivos_enc, dificuldades_desafios_enc, texto_confirmado_enc, origem, data_hora_criacao FROM registro_diario"
                + where + " ORDER BY data_hora_criacao DESC, id DESC";
        if (search.isEmpty()) {
            sql += " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
            args.add(pagina * tamanho); args.add(tamanho + 1);
        }
        // A textual query must scan the patient's candidates; no plaintext index or shadow copy.
        // JDBC stream bounds heap use and is closed even when limit terminates early.
        try (var rows = jdbc.queryForStream(sql,
                (rs, n) -> new Registro(rs.getLong("id"), rs.getString("nivel_humor"),
                        crypto.decrypt(rs.getString("pontos_positivos_enc"), "registro_diario.pontos_positivos"),
                        crypto.decrypt(rs.getString("dificuldades_desafios_enc"), "registro_diario.dificuldades_desafios"),
                        crypto.decrypt(rs.getString("texto_confirmado_enc"), "registro_diario.texto_confirmado"),
                        rs.getString("origem"), rs.getTimestamp("data_hora_criacao").toLocalDateTime().toString()), args.toArray())) {
            var filtered = rows.filter(r -> search.isEmpty() || contains(r.pontosPositivos(),search)
                    || contains(r.dificuldadesDesafios(),search) || contains(r.textoConfirmado(),search));
            if (!search.isEmpty()) filtered = filtered.skip((long) pagina * tamanho);
            var result = filtered.limit(tamanho + 1L).toList();
            return new Pagina(List.copyOf(result.subList(0, Math.min(result.size(), tamanho))), pagina, result.size() > tamanho);
        }
    }
    private static boolean contains(String text, String search) {
        return text != null && text.toLowerCase(Locale.ROOT).contains(search);
    }
}
