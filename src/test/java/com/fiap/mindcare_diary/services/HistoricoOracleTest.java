package com.fiap.mindcare_diary.services;

import com.fiap.mindcare_diary.models.*;
import com.fiap.mindcare_diary.models.enums.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import static org.junit.jupiter.api.Assertions.*;

/** Executar apenas no esquema descartável MINDCARE_TEST. Transação revertida ao final. */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class HistoricoOracleTest {
    @Autowired EntityManager em;
    @Autowired HistoricoService service;
    @Autowired org.springframework.jdbc.core.JdbcTemplate jdbc;
    @Autowired com.fiap.mindcare_diary.security.storage.RecordCrypto crypto;

    @Test
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED)
    void migraTextoLegadoNoOracleSemPerderConteudo() throws Exception {
        // This entire schema is disposable (test profile, create-drop).
        jdbc.execute("ALTER TABLE registro_diario ADD pontos_positivos CLOB");
        jdbc.update("INSERT INTO registro_diario (origem, pontos_positivos) VALUES ('TRADITIONAL', 'Relato legado Oracle')");
        Long id = jdbc.queryForObject("SELECT MAX(id) FROM registro_diario",Long.class);
        try (var connection = jdbc.getDataSource().getConnection()) {
            com.fiap.mindcare_diary.security.storage.EncryptionMigration.migrate(connection,crypto,false);
            com.fiap.mindcare_diary.security.storage.EncryptionMigration.migrate(connection,crypto,false);
        }
        var raw = jdbc.queryForObject("SELECT pontos_positivos_enc FROM registro_diario WHERE id=?", (rs,n)->rs.getString(1),id);
        assertEquals("Relato legado Oracle",crypto.decrypt(raw,"registro_diario.pontos_positivos"));
        assertNull(jdbc.queryForObject("SELECT pontos_positivos FROM registro_diario WHERE id=?",(rs,n)->rs.getString(1),id));
        jdbc.update("DELETE FROM registro_diario WHERE id=?",id);
    }
    @Autowired com.fiap.mindcare_diary.repositories.RegistroDiarioRepository registros;

    @Test void jdbcEJpaGuardamCiphertextEMantemLeituraDoDiarioERelatorio() {
        var patient = paciente(); em.flush();
        var r = new RegistroDiario(); r.setPaciente(patient); r.setOrigem(OrigemRegistro.CHAT);
        r.setDataHoraCriacao(LocalDateTime.now());
        String text = "Relato privado com emoção 😀 ".repeat(1500);
        r.setTextoConfirmado(text); registros.saveAndFlush(r);
        String raw = jdbc.queryForObject("SELECT texto_confirmado_enc FROM registro_diario WHERE paciente_id = ?",
                (rs,n) -> rs.getString(1),patient.getId());
        assertTrue(raw.startsWith("mc1:")); assertFalse(raw.contains("Relato"));
        assertEquals(text, registros.findByPacienteAndIdRequisicao(patient,r.getIdRequisicao()).orElseThrow().getTextoConfirmado());
        var report = new RelatorioSemanal(); report.setPaciente(patient); report.setRelatorioIA(text);
        report.setObservacoes("Observação privada"); report.setResumo("Resumo privado"); report.setRecomendacoes("Recomendação privada");
        em.persist(report); em.flush(); Long id=report.getId(); em.clear();
        var loaded = em.find(RelatorioSemanal.class,id);
        assertEquals(text, loaded.getRelatorioIA()); assertEquals("Resumo privado",loaded.getResumo());
        raw=jdbc.queryForObject("SELECT relatorio_ia_enc FROM relatorio_semanal WHERE id=?",(rs,n)->rs.getString(1),id);
        assertTrue(raw.startsWith("mc1:")); assertFalse(raw.contains("Relato"));
        assertEquals(text, em.createQuery("select r from RegistroDiario r where r.paciente.id=:id",RegistroDiario.class)
                .setParameter("id",patient.getId()).getSingleResult().getTextoConfirmado());
    }

    @Test void permiteDoisRegistrosTradicionaisSemChaveEnviadaPeloApp() {
        var patient = paciente(); em.flush();
        for (int i=0; i<2; i++) {
            var r = new RegistroDiario(); r.setPaciente(patient); r.setNivelHumor(NivelHumor.BOM);
            r.setDataHoraCriacao(LocalDateTime.now()); r.setPontosPositivos("Dia " + i);
            registros.saveAndFlush(r); assertNotNull(r.getIdRequisicao());
        }
        assertEquals(2, service.buscar(patient.getId(),null,null,null,null,null,0,20).registros().size());
    }

    @Test void consultaRealCombinaFiltrosPaginaESeparaPacientes() {
        var patient = paciente();
        var other = paciente();
        relato(patient, OrigemRegistro.TRADITIONAL, "Passeio 100%", LocalDateTime.of(2026,10,1,23,59));
        relato(patient, OrigemRegistro.CHAT, "Passeio 100% Chat", LocalDateTime.of(2026,10,2,12,0));
        relato(other, OrigemRegistro.CHAT, "EXCLUSIVO_OUTRO", LocalDateTime.of(2026,10,2,12,0));
        em.flush();
        var first = service.buscar(patient.getId(), "100%", null, null, NivelHumor.BOM, null, 0, 1);
        assertEquals(1, first.registros().size()); assertTrue(first.temMais());
        assertEquals("CHAT", first.registros().getFirst().origem());
        var last = service.buscar(patient.getId(), "100%", null, null, NivelHumor.BOM, null, 1, 1);
        assertFalse(last.temMais()); assertEquals("TRADITIONAL", last.registros().getFirst().origem());
        assertEquals(1, service.buscar(patient.getId(), null, LocalDate.of(2026,10,1), LocalDate.of(2026,10,1), null, OrigemRegistro.TRADITIONAL,0,20).registros().size());
        assertTrue(service.buscar(patient.getId(), "EXCLUSIVO_OUTRO", null,null,null,null,0,20).registros().isEmpty());
    }
    private Paciente paciente() {
        var p = new Paciente(); p.setNomeUsuario("historico-test-" + java.util.UUID.randomUUID());
        p.setUserRole(UserRole.PACIENTE); p.setAtivo(true); em.persist(p); return p;
    }
    private void relato(Paciente p, OrigemRegistro origem, String texto, LocalDateTime data) {
        var r = new RegistroDiario(); r.setPaciente(p); r.setOrigem(origem); r.setNivelHumor(NivelHumor.BOM); r.setDataHoraCriacao(data);
        r.setIdRequisicao(java.util.UUID.randomUUID());
        if (origem == OrigemRegistro.CHAT) r.setTextoConfirmado(texto); else r.setPontosPositivos(texto);
        em.persist(r);
    }
}
