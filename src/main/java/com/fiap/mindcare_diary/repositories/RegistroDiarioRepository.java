package com.fiap.mindcare_diary.repositories;

import com.fiap.mindcare_diary.exceptions.PacienteNaoEncontradoException;
import com.fiap.mindcare_diary.models.Paciente;
import com.fiap.mindcare_diary.models.RegistroDiario;
import com.fiap.mindcare_diary.models.enums.NivelHumor;
import com.fiap.mindcare_diary.models.enums.OrigemRegistro;
import jakarta.transaction.Transactional;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.nio.ByteBuffer;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RegistroDiarioRepository {

    private final JdbcTemplate jdbcTemplate;
    private final com.fiap.mindcare_diary.security.storage.RecordCrypto crypto;
    private final PacienteRepository pacienteRepository;

    public RegistroDiarioRepository(JdbcTemplate jdbcTemplate, PacienteRepository pacienteRepository, com.fiap.mindcare_diary.security.storage.RecordCrypto crypto) {
        this.jdbcTemplate = jdbcTemplate;
        this.crypto = crypto;
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
                this::mapRegistro,
                pacienteId
        );
    }

    public List<RegistroDiario> deletarTodos(String pacienteNomeUsuario) {
        Optional<Paciente> optionalPaciente = pacienteRepository.findByNomeUsuario(pacienteNomeUsuario);
        if(optionalPaciente.isEmpty()) {
            throw new PacienteNaoEncontradoException("Paciente não encontrado.");
        }
        Paciente paciente = optionalPaciente.get();
        Long pacienteId = paciente.getId();
        String sql = "delete * from registro_diario where paciente_id = ?";

        return jdbcTemplate.query(
                sql,
                this::mapRegistro,
                pacienteId
        );
    }

    public List<RegistroDiario> carregarUltimosRegistrosDiarios(
            String pacienteNomeUsuario) {

        Optional<Paciente> optionalPaciente = this.pacienteRepository.findByNomeUsuario(pacienteNomeUsuario);
        if(optionalPaciente.isEmpty()) {
            throw new PacienteNaoEncontradoException("Paciente não encontrado.");
        }


        return jdbcTemplate.execute(
                (CallableStatementCreator) connection -> {
                    CallableStatement cs = connection.prepareCall(
                            "{ ? = call MINDCARE.CARREGA_ULTIMOS_REGISTROS_DIARIOS(?) }"
                    );
                    cs.registerOutParameter(1, Types.REF_CURSOR);
                    cs.setString(2, pacienteNomeUsuario);
                    return cs;
                },
                cs -> {
                    cs.execute();
                    List<RegistroDiario> registros = new ArrayList<>();
                    try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                        while (rs.next()) {
                            RegistroDiario registro = mapRegistro(rs, 0);
                            registro.setPaciente(optionalPaciente.get());
                            registros.add(registro);
                        }
                    }
                    return registros;
                }
        );
    }

    private RegistroDiario mapRegistro(ResultSet rs, int rowNum) throws java.sql.SQLException {

        RegistroDiario registro = new RegistroDiario();

        registro.setId(rs.getLong("id"));
        registro.setNivelHumor(rs.getString("nivel_humor") == null || rs.getString("nivel_humor").isBlank()
                ? null
                : NivelHumor.valueOf(rs.getString("nivel_humor")));

        registro.setPontosPositivos(crypto.decrypt(rs.getString("pontos_positivos_enc"), "registro_diario.pontos_positivos"));
        registro.setDificuldadesDesafios(crypto.decrypt(rs.getString("dificuldades_desafios_enc"), "registro_diario.dificuldades_desafios"));

        Timestamp dataHoraCriacao = rs.getTimestamp("data_hora_criacao");
        registro.setDataHoraCriacao(dataHoraCriacao == null ? null : dataHoraCriacao.toLocalDateTime());

        registro.setTextoConfirmado(crypto.decrypt(rs.getString("texto_confirmado_enc"), "registro_diario.texto_confirmado"));

        registro.setOrigem(rs.getString("origem") == null || rs.getString("origem").isBlank()
                ? OrigemRegistro.TRADITIONAL
                : OrigemRegistro.valueOf(rs.getString("origem")));

        String requestId = rs.getString("id_requisicao");
        registro.setIdRequisicao(requestId == null ? null : UUID.fromString(requestId));

        return registro;
    }

    public Optional<RegistroDiario> findByPacienteAndIdRequisicao(Paciente paciente, UUID idRequisicao) {
        Long pacienteId = paciente.getId();
        String uuidHex = idRequisicao == null ? "": idRequisicao.toString();
        String sql = "select * from registro_diario where paciente_id = ? and id_requisicao = ?";

        List<RegistroDiario> registros = jdbcTemplate.query(
                sql,
                this::mapRegistro,
                pacienteId,
                uuidHex
        );

        return registros.stream().findFirst();

    }

    @Transactional
    public void saveAndFlush(RegistroDiario registroDiario) {
        // Oracle considera iguais as chaves compostas com mesmo paciente e UUID nulo.
        // Preserva chaves do Chat e atribui uma chave aos registros tradicionais sem UUID.
        if (registroDiario.getIdRequisicao() == null) registroDiario.setIdRequisicao(UUID.randomUUID());
        Long pacienteId = registroDiario.getPaciente().getId();
        String nivelHumor = registroDiario.getNivelHumor() == null ? "" : registroDiario.getNivelHumor().name();
        String pontosPositivos = crypto.encrypt(registroDiario.getPontosPositivos(), "registro_diario.pontos_positivos");
        String dificuldadesDesafios = crypto.encrypt(registroDiario.getDificuldadesDesafios(), "registro_diario.dificuldades_desafios");
        Timestamp dataHoraCriacao = registroDiario.getDataHoraCriacao() == null
                ? null
                : Timestamp.valueOf(registroDiario.getDataHoraCriacao());
        String textConfirmado = crypto.encrypt(registroDiario.getTextoConfirmado(), "registro_diario.texto_confirmado");
        String origem = registroDiario.getOrigem() == null ? "" : registroDiario.getOrigem().name();
        String idRequisicao = registroDiario.getIdRequisicao() == null ? "" : registroDiario.getIdRequisicao().toString();
        String sql = "insert into registro_diario (paciente_id, nivel_humor, pontos_positivos_enc, dificuldades_desafios_enc, data_hora_criacao, texto_confirmado_enc, origem, id_requisicao) values (?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                pacienteId, nivelHumor, pontosPositivos, dificuldadesDesafios, dataHoraCriacao, textConfirmado, origem, idRequisicao);
    }
}
