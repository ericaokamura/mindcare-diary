package com.fiap.mindcare_diary.services.mia;

import com.fiap.mindcare_diary.models.Paciente;
import com.fiap.mindcare_diary.models.RegistroDiario;
import com.fiap.mindcare_diary.models.dtos.MiaRegistroRequest;
import com.fiap.mindcare_diary.models.enums.*;
import com.fiap.mindcare_diary.repositories.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import java.util.*;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = {"spring.sql.init.mode=never", "spring.jpa.hibernate.ddl-auto=create-drop", "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"}, showSql = false)
@Import(MiaRegistroService.class)
class MiaPersistenceTest {
    @Autowired PacienteRepository patients;
    @Autowired RegistroDiarioRepository records;
    @Autowired MiaRegistroService service;

    private Paciente patient(String name) {
        var p = new Paciente(); p.setNomeUsuario(name); p.setAtivo(true); p.setUserRole(UserRole.PACIENTE);
        return patients.saveAndFlush(p);
    }

    @Test void writesOneRecordOnRetryAndKeepsPatientsSeparate() {
        var first = patient("primeiro"); var second = patient("segundo");
        var request = new MiaRegistroRequest(UUID.randomUUID(), "Relato fictício confirmado", null);
        var auth1 = UsernamePasswordAuthenticationToken.authenticated(Optional.of(first), null, List.of());
        var auth2 = UsernamePasswordAuthenticationToken.authenticated(Optional.of(second), null, List.of());
        var saved = service.save(auth1, request);
        assertEquals(saved.getId(), service.save(auth1, request).getId());
        assertNotEquals(saved.getId(), service.save(auth2, request).getId());
        assertEquals(1, records.findAllByPaciente(first).size());
        assertEquals("Relato fictício confirmado", records.findAllByPaciente(first).getFirst().getTextoConfirmado());
        assertEquals(2, records.count());
    }

    @Test void traditionalRecordsRemainReadableAlongsideChat() {
        var p = patient("tradicional");
        var old = new RegistroDiario(); old.setPaciente(p); old.setPontosPositivos("Dia tranquilo");
        old.setNivelHumor(NivelHumor.BOM); records.saveAndFlush(old);
        assertEquals(OrigemRegistro.TRADITIONAL, records.findAllByPaciente(p).getFirst().getOrigem());
        assertNull(records.findAllByPaciente(p).getFirst().getTextoConfirmado());
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void concurrentRetriesAreSerializedByDatabaseLock() throws Exception {
        var p = patient("concorrente");
        var auth = UsernamePasswordAuthenticationToken.authenticated(p, null, List.of());
        var request = new MiaRegistroRequest(UUID.randomUUID(), "Relato concorrente", "BOM");
        try (var executor = Executors.newFixedThreadPool(2)) {
            var first = executor.submit(() -> service.save(auth, request));
            var second = executor.submit(() -> service.save(auth, request));
            assertEquals(first.get(10, TimeUnit.SECONDS).getId(), second.get(10, TimeUnit.SECONDS).getId());
            assertEquals(1, records.findAllByPaciente(p).size());
        } finally {
            records.deleteAll(records.findAllByPaciente(p));
            patients.delete(p);
        }
    }
}
