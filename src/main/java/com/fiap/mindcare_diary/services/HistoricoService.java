package com.fiap.mindcare_diary.services;

import com.fiap.mindcare_diary.models.enums.NivelHumor;
import com.fiap.mindcare_diary.models.enums.OrigemRegistro;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Consulta independente das rotinas PL/SQL existentes. Nunca recebe o titular do cliente. */
@Service
public class HistoricoService {
    private final JdbcTemplate jdbc;
    public HistoricoService(JdbcTemplate jdbc) { this.jdbc = jdbc; }
    public record Registro(Long id, String nivelHumor, String pontosPositivos, String dificuldadesDesafios,
                           String textoConfirmado, String origem, String dataHoraCriacao) {}
    public record Pagina(List<Registro> registros, int pagina, boolean temMais) {}

    public Pagina buscar(Long pacienteId, String texto, LocalDate inicio, LocalDate fim,
                         NivelHumor humor, OrigemRegistro origem, int pagina, int tamanho) {
        if (pagina < 0 || pagina > 100000 || tamanho < 1 || tamanho > 50 ||
                (texto != null && texto.length() > 200) || (inicio != null && fim != null && inicio.isAfter(fim)) ||
                (fim != null && fim.getYear() > 9998))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Filtros inválidos.");
        var where = new StringBuilder(" WHERE paciente_id = ?");
        List<Object> args = new ArrayList<>(); args.add(pacienteId);
        if (inicio != null) { where.append(" AND data_hora_criacao >= ?"); args.add(java.sql.Timestamp.valueOf(inicio.atStartOfDay())); }
        if (fim != null) { where.append(" AND data_hora_criacao < ?"); args.add(java.sql.Timestamp.valueOf(fim.plusDays(1).atStartOfDay())); }
        if (humor != null) { where.append(" AND nivel_humor = ?"); args.add(humor.name()); }
        if (origem != null) { where.append(" AND origem = ?"); args.add(origem.name()); }
        if (texto != null && !texto.isBlank()) {
            where.append(" AND (LOWER(texto_confirmado) LIKE ? ESCAPE '!' OR LOWER(pontos_positivos) LIKE ? ESCAPE '!' OR LOWER(dificuldades_desafios) LIKE ? ESCAPE '!')");
            String busca = "%" + texto.trim().toLowerCase(java.util.Locale.ROOT).replace("!", "!!").replace("%", "!%").replace("_", "!_") + "%";
            args.add(busca); args.add(busca); args.add(busca);
        }
        args.add(pagina * tamanho); args.add(tamanho + 1);
        var rows = jdbc.query("SELECT id, nivel_humor, pontos_positivos, dificuldades_desafios, texto_confirmado, origem, data_hora_criacao FROM registro_diario" + where +
                " ORDER BY data_hora_criacao DESC, id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY",
                (rs, n) -> new Registro(rs.getLong("id"), rs.getString("nivel_humor"), rs.getString("pontos_positivos"),
                        rs.getString("dificuldades_desafios"), rs.getString("texto_confirmado"), rs.getString("origem"),
                        rs.getTimestamp("data_hora_criacao").toLocalDateTime().toString()), args.toArray());
        return new Pagina(List.copyOf(rows.subList(0, Math.min(rows.size(), tamanho))), pagina, rows.size() > tamanho);
    }
}
