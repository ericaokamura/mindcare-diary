package com.fiap.mindcare_diary.services.mia;

import com.fiap.mindcare_diary.models.Paciente;
import com.fiap.mindcare_diary.models.RegistroDiario;
import com.fiap.mindcare_diary.models.dtos.MiaRegistroRequest;
import com.fiap.mindcare_diary.models.enums.*;
import com.fiap.mindcare_diary.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MiaRegistroServiceTest {

    PacienteRepository patients;
    RegistroDiarioRepository records;
    MiaRegistroService service;
    Paciente patient;
    Authentication auth;

    @BeforeEach void setup() {
        patients = mock(PacienteRepository.class);
        records = mock(RegistroDiarioRepository.class);
        service = new MiaRegistroService(patients, records);
        patient = new Paciente(); patient.setNomeUsuario("paciente"); patient.setId(7L);
        patient.setAtivo(true); patient.setUserRole(UserRole.PACIENTE);
        patient.setSenha("nao-retornar"); patient.setToken("nao-retornar");
        auth = UsernamePasswordAuthenticationToken.authenticated(Optional.of(patient), null, List.of());
        when(patients.findForDiaryUpdate("paciente")).thenReturn(Optional.of(patient));
        doNothing().when(records).saveAndFlush(any());
    }

    @Test void persistsOnlyConfirmedTextAndUsesServerOwnershipTimeAndOrigin() {
        var key = UUID.randomUUID();
        service.save(auth, new MiaRegistroRequest(key, "  Um relato revisado  ", null));
        var captor = ArgumentCaptor.forClass(RegistroDiario.class);
        verify(records).saveAndFlush(captor.capture());
        var record = captor.getValue();
        assertSame(patient, record.getPaciente());
        assertEquals("Um relato revisado", record.getTextoConfirmado());
        assertEquals(OrigemRegistro.CHAT, record.getOrigem());
        assertEquals(NivelHumor.SEM_DEFINICAO, record.getNivelHumor());
        assertEquals("", record.getPontosPositivos());
        assertEquals("", record.getDificuldadesDesafios());
        assertNotNull(record.getDataHoraCriacao());
        assertEquals(key, record.getIdRequisicao());
    }

    @Test void retryReturnsSameRecordWithoutSavingAgain() {
        var key = UUID.randomUUID();
        var record = new RegistroDiario(); record.setId(21L); record.setTextoConfirmado("texto");
        record.setNivelHumor(NivelHumor.BOM); record.setDataHoraCriacao(LocalDateTime.now());
        when(records.findByPacienteAndIdRequisicao(patient, key)).thenReturn(Optional.of(record));
        verify(records, never()).saveAndFlush(any());
        var error = assertThrows(ResponseStatusException.class, () -> service.save(auth, new MiaRegistroRequest(key, "outro texto", "BOM")));
        assertEquals(409, error.getStatusCode().value());
    }

    @Test void validatesBeforeTouchingDatabase() {
        for (var req : List.of(new MiaRegistroRequest(null, "texto", null),
                new MiaRegistroRequest(UUID.randomUUID(), " ", null),
                new MiaRegistroRequest(UUID.randomUUID(), "x".repeat(20001), null),
                new MiaRegistroRequest(UUID.randomUUID(), "texto", "inventado"))) {
            assertEquals(400, assertThrows(ResponseStatusException.class, () -> service.save(auth, req)).getStatusCode().value());
        }
        verifyNoInteractions(records);
        verify(patients, never()).findForDiaryUpdate(anyString());
    }

    @Test void deniesUnauthenticatedProfessionalAndInactivePatient() {
        var request = new MiaRegistroRequest(UUID.randomUUID(), "texto", null);
        assertEquals(401, assertThrows(ResponseStatusException.class, () -> service.save(null, request)).getStatusCode().value());
        patient.setUserRole(UserRole.PROFISSIONAL);
        assertEquals(403, assertThrows(ResponseStatusException.class, () -> service.save(auth, request)).getStatusCode().value());
        patient.setUserRole(UserRole.PACIENTE); patient.setAtivo(false);
        assertEquals(403, assertThrows(ResponseStatusException.class, () -> service.save(auth, request)).getStatusCode().value());
        verifyNoInteractions(records);
    }
}
