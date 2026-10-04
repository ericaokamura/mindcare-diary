package com.fiap.mindcare_diary.repositories;

import com.fiap.mindcare_diary.exceptions.PacienteNaoEncontradoException;
import com.fiap.mindcare_diary.models.Paciente;
import com.fiap.mindcare_diary.models.RegistroDiario;
import com.fiap.mindcare_diary.models.enums.NivelHumor;
import com.fiap.mindcare_diary.models.enums.OrigemRegistro;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RegistroDiarioRepository {

    private final JdbcTemplate jdbcTemplate;
    private final PacienteRepository pacienteRepository;

    public RegistroDiarioRepository(JdbcTemplate jdbcTemplate, PacienteRepository pacienteRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.pacienteRepository = pacienteRepository;
    }

    public List<RegistroDiario> carregarTodosRegistrosDiarios(String pacienteNomeUsuario) {
        Optional<Paciente> optionalPaciente = pacienteRepository.findByNomeUsuario(pacienteNomeUsuario);
        if(optionalPaciente.isEmpty()) {
            throw new PacienteNaoEncontradoException("Paciente não encontrado.");
        }
        Paciente paciente = optionalPaciente.get();
        Long pacienteId = paciente.getId();
        String sql = "select * from registro_diario where paciente_id = ?";

        return jdbcTemplate.query(
                sql,
                registroDiarioRowMapper,
                pacienteId
        );
    }

    public List<RegistroDiario> carregarUltimosRegistrosDiarios(String pacienteNomeUsuario) {

        return jdbcTemplate.queryForObject(
                "CALL carrega_ultimos_registros_diarios(?)",
                List.class,
                pacienteNomeUsuario
        );
    }

    private final RowMapper<RegistroDiario> registroDiarioRowMapper = (rs, rowNum) -> {
        RegistroDiario registro = new RegistroDiario();

        registro.setId(rs.getLong("id"));
        registro.setNivelHumor(rs.getString("nivel_humor") == null || rs.getString("nivel_humor").isBlank()
                ? null
                : NivelHumor.valueOf(rs.getString("nivel_humor")));

        registro.setPontosPositivos(rs.getString("pontos_positivos"));
        registro.setDificuldadesDesafios(rs.getString("dificuldades_desafios"));

        Timestamp dataHoraCriacao = rs.getTimestamp("data_hora_criacao");
        registro.setDataHoraCriacao(dataHoraCriacao == null ? null : dataHoraCriacao.toLocalDateTime());

        registro.setTextoConfirmado(rs.getString("texto_confirmado"));

        registro.setOrigem(rs.getString("origem") == null || rs.getString("origem").isBlank()
                ? OrigemRegistro.TRADITIONAL
                : OrigemRegistro.valueOf(rs.getString("origem")));

        return registro;
    };

    public Optional<RegistroDiario> findByPacienteAndIdRequisicao(Paciente paciente, UUID idRequisicao) {
        Long pacienteId = paciente.getId();
        String uuidHex = idRequisicao == null ? "": idRequisicao.toString().replace("-", "");
        String sql = "select * from registro_diario where paciente_id = ? and id_requisicao = HEXTORAW(?)";

        List<RegistroDiario> registros = jdbcTemplate.query(
                sql,
                registroDiarioRowMapper,
                pacienteId,
                uuidHex
        );

        return registros.stream().findFirst();

    }

    @Transactional
    public void saveAndFlush(RegistroDiario registroDiario) {
        Long pacienteId = registroDiario.getPaciente().getId();
        String nivelHumor = registroDiario.getNivelHumor() == null ? "" : registroDiario.getNivelHumor().name();
        String pontosPositivos = registroDiario.getPontosPositivos();
        String dificuldadesDesafios = registroDiario.getDificuldadesDesafios();
        Timestamp dataHoraCriacao = registroDiario.getDataHoraCriacao() == null
                ? null
                : Timestamp.valueOf(registroDiario.getDataHoraCriacao());
        String textConfirmado = registroDiario.getTextoConfirmado();
        String origem = registroDiario.getOrigem() == null ? "" : registroDiario.getOrigem().name();
        String idRequisicao = registroDiario.getIdRequisicao() == null ? "" : registroDiario.getIdRequisicao().toString().replace("-", "");
        String sql = "insert into registro_diario (paciente_id, nivel_humor, pontos_positivos, dificuldades_desafios, data_hora_criacao, texto_confirmado, origem, id_requisicao) values (?, ?, ?, ?, ?, ?, ?, HEXTORAW(?))";

        jdbcTemplate.update(sql,
                pacienteId, nivelHumor, pontosPositivos, dificuldadesDesafios, dataHoraCriacao, textConfirmado, origem, idRequisicao);
    }
}
