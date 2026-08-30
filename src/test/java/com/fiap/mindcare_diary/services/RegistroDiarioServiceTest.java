package com.fiap.mindcare_diary.services;

import com.fiap.mindcare_diary.models.Paciente;
import com.fiap.mindcare_diary.repositories.PacienteRepository;
import com.fiap.mindcare_diary.repositories.RegistroDiarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistroDiarioServiceTest {
    @Mock RegistroDiarioRepository registroDiarioRepository;
    @Mock PacienteRepository pacienteRepository;
    @InjectMocks RegistroDiarioService service;

    @Test
    void deveLancarExcecaoAoSalvarRegistroParaPacienteInexistente() {
        lenient().when(pacienteRepository.findByNomeUsuario("x")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> service.salvarRegistroDiario("x", new com.fiap.mindcare_diary.models.dtos.RegistroDiarioDTO()));
        verify(registroDiarioRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoConsultarRegistrosDePacienteInexistente() {
        when(pacienteRepository.findByNomeUsuario("x")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> service.retornarRegistrosDiarios("x"));
        verify(registroDiarioRepository, never()).findAllByPaciente(any());
    }
}
