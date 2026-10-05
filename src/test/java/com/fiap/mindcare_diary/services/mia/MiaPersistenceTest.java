package com.fiap.mindcare_diary.services.mia;

import com.fiap.mindcare_diary.models.Paciente;
import com.fiap.mindcare_diary.models.RegistroDiario;
import com.fiap.mindcare_diary.models.dtos.MiaRegistroRequest;
import com.fiap.mindcare_diary.models.enums.*;
import com.fiap.mindcare_diary.repositories.*;
import org.junit.jupiter.api.Test;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import java.util.*;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class MiaPersistenceTest {

    @Autowired
    private MiaRegistroService miaRegistroService;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private RegistroDiarioRepository registroDiarioRepository;

    @MockitoBean
    private VectorStore vectorStore;

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void writesOneRecordOnRetryAndKeepsPatientsSeparate() {
        var first = new Paciente();
        first.setNomeUsuario("primeiro");
        first.setAtivo(true);
        first.setUserRole(UserRole.PACIENTE);

        var second = new Paciente();
        second.setNomeUsuario("segundo");
        second.setAtivo(true);
        second.setUserRole(UserRole.PACIENTE);

        pacienteRepository.saveAndFlush(first);
        pacienteRepository.saveAndFlush(second);

        UUID requestId = UUID.randomUUID();

        var request1 = new MiaRegistroRequest(
                requestId,
                "Relato fictício confirmado 1",
                NivelHumor.SEM_DEFINICAO.name()
        );
        var request2 = new MiaRegistroRequest(
                requestId,
                "Relato fictício confirmado 2",
                NivelHumor.SEM_DEFINICAO.name()
        );

        var auth1 = UsernamePasswordAuthenticationToken.authenticated(
                first, "senha1", List.of()
        );
        var auth2 = UsernamePasswordAuthenticationToken.authenticated(
                second, "senha2", List.of()
        );

        miaRegistroService.save(auth1, request1);
        miaRegistroService.save(auth1, request1);
        miaRegistroService.save(auth2, request2);

        Optional<RegistroDiario> optionalRegistroDiario1 = registroDiarioRepository.findByPacienteAndIdRequisicao(first, requestId);
        Optional<RegistroDiario> optionalRegistroDiario2 = registroDiarioRepository.findByPacienteAndIdRequisicao(second, requestId);

        assertTrue(optionalRegistroDiario1.isPresent());
        assertTrue(optionalRegistroDiario2.isPresent());

    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void concurrentRetriesAreSerializedByDatabaseLock() throws Exception {
        var paciente = new Paciente();
        paciente.setNomeUsuario("concorrente");
        paciente.setAtivo(true);
        paciente.setUserRole(UserRole.PACIENTE);

        paciente = pacienteRepository.saveAndFlush(paciente);

        var auth = UsernamePasswordAuthenticationToken.authenticated(
                paciente,
                "pwd",
                List.of()
        );

        UUID requestId = UUID.randomUUID();
        var request1 = new MiaRegistroRequest(
                requestId,
                "Relatório concorrente 1",
                "BOM"
        );
        var request2 = new MiaRegistroRequest(
                requestId,
                "Relatório concorrente 2",
                "BOM"
        );

        try (var executor = Executors.newFixedThreadPool(2)) {
            Future<?> first = executor.submit(
                    () -> miaRegistroService.save(auth, request1)
            );
            Future<?> second = executor.submit(
                    () -> miaRegistroService.save(auth, request2)
            );
            int accepted = 0, rejected = 0;
            for (Future<?> result : List.of(first, second)) {
                try { result.get(); accepted++; }
                catch (ExecutionException exception) {
                    var conflict = assertInstanceOf(ResponseStatusException.class, exception.getCause());
                    assertEquals(409, conflict.getStatusCode().value()); rejected++;
                }
            }
            assertEquals(1, accepted); assertEquals(1, rejected);
            assertEquals(1, registroDiarioRepository.carregarTodosRegistrosDiarios(paciente.getNomeUsuario()).size());
        }
    }
}
