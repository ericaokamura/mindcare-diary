package com.fiap.mindcare_diary.services;

import com.fiap.mindcare_diary.models.Consulta;
import com.fiap.mindcare_diary.models.Paciente;
import com.fiap.mindcare_diary.models.Profissional;
import com.fiap.mindcare_diary.models.dtos.ConsultaDTO;
import com.fiap.mindcare_diary.repositories.ConsultaRepository;
import com.fiap.mindcare_diary.repositories.PacienteRepository;
import com.fiap.mindcare_diary.repositories.ProfissionalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaServiceTest {
    @Mock ConsultaRepository consultaRepository;
    @Mock PacienteRepository pacienteRepository;
    @Mock ProfissionalRepository profissionalRepository;
    @InjectMocks ConsultaService service;

    @Test
    void deveRetornarConsultasDoPaciente() {
        Paciente paciente = new Paciente();
        paciente.setNomeUsuario("paciente");
        Consulta consulta = new Consulta();
        consulta.setPaciente(paciente);
        consulta.setDataHoraConsulta(java.time.LocalDateTime.of(2026, 8, 28, 10, 0));
        when(pacienteRepository.findByNomeUsuario("paciente")).thenReturn(Optional.of(paciente));
        when(consultaRepository.findAllByPaciente(paciente)).thenReturn(List.of(consulta));

        assertEquals(1, service.retornarConsultasPorPaciente("paciente").size());
    }

    @Test
    void deveLancarExcecaoQuandoPacienteNaoExistir() {
        when(pacienteRepository.findByNomeUsuario("x")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.retornarConsultasPorPaciente("x"));
        verify(consultaRepository, never()).findAllByPaciente(any());
    }

    @Test
    void deveRetornarConsultasDoProfissional() {
        Profissional profissional = new Profissional();
        profissional.setNomeUsuario("prof");
        Consulta consulta = new Consulta();
        consulta.setProfissional(profissional);
        consulta.setDataHoraConsulta(java.time.LocalDateTime.of(2026, 8, 28, 10, 0));
        when(profissionalRepository.findByNomeUsuario("prof")).thenReturn(Optional.of(profissional));
        when(consultaRepository.findAllByProfissional(profissional)).thenReturn(List.of(consulta));

        assertEquals(1, service.retornarConsultasPorProfissional("prof").size());
    }

    @Test
    void deveLancarExcecaoQuandoConsultaNaoExistir() {
        when(consultaRepository.findByNumber("12345")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.atualizarConsulta("12345", new ConsultaDTO()));
        verify(consultaRepository, never()).save(any());
    }
}
