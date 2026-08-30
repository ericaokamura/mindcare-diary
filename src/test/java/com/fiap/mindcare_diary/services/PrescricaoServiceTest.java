package com.fiap.mindcare_diary.services;

import com.fiap.mindcare_diary.models.Paciente;
import com.fiap.mindcare_diary.models.Prescription;
import com.fiap.mindcare_diary.models.Profissional;
import com.fiap.mindcare_diary.repositories.PacienteRepository;
import com.fiap.mindcare_diary.repositories.PrescriptionRepository;
import com.fiap.mindcare_diary.repositories.ProfissionalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrescricaoServiceTest {
    @Mock PrescriptionRepository prescriptionRepository;
    @Mock PacienteRepository pacienteRepository;
    @Mock ProfissionalRepository profissionalRepository;
    @InjectMocks PrescricaoService service;

    @Test
    void deveLancarExcecaoQuandoProfissionalNaoExistir() {
        when(profissionalRepository.findByNomeUsuario("prof")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class,
                () -> service.retornarPrescricaoPorNumber("pac", "prof", "123"));
    }

    @Test
    void deveLancarExcecaoQuandoPacienteNaoExistir() {
        Profissional prof = new Profissional();
        when(profissionalRepository.findByNomeUsuario("prof")).thenReturn(Optional.of(prof));
        when(pacienteRepository.findByNomeUsuario("pac")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> service.retornarPrescricaoPorNumber("pac", "prof", "123"));
    }

    @Test
    void deveLancarExcecaoQuandoPrescricaoNaoExistir() {
        Profissional prof = new Profissional();
        Paciente pac = new Paciente();
        when(profissionalRepository.findByNomeUsuario("prof")).thenReturn(Optional.of(prof));
        when(pacienteRepository.findByNomeUsuario("pac")).thenReturn(Optional.of(pac));
        when(prescriptionRepository.findByPacienteAndProfissionalAndNumber(pac, prof, "123"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> service.retornarPrescricaoPorNumber("pac", "prof", "123"));
    }
}
