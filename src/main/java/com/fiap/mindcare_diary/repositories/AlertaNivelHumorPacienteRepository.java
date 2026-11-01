package com.fiap.mindcare_diary.repositories;

import jakarta.transaction.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AlertaNivelHumorPacienteRepository {

    private final JdbcTemplate jdbcTemplate;

    public AlertaNivelHumorPacienteRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void registraAlertaNivelHumorPaciente(String pacienteNomeUsuario) {

        jdbcTemplate.update(
                "CALL PKG_MINDCARE_DIARIO.REGISTRA_ALERTA_NIVEL_HUMOR_PACIENTE(?)",
                pacienteNomeUsuario
        );
    }
}
