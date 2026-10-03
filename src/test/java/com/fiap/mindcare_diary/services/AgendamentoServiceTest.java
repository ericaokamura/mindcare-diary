package com.fiap.mindcare_diary.services;

import com.fiap.mindcare_diary.exceptions.PacienteNaoEncontradoException;
import com.fiap.mindcare_diary.exceptions.ProfissionalNaoEncontradoException;
import com.fiap.mindcare_diary.models.Paciente;
import com.fiap.mindcare_diary.models.dtos.PacienteDTO;
import com.fiap.mindcare_diary.models.dtos.ProfissionalDTO;
import com.fiap.mindcare_diary.repositories.AgendamentoRepository;
import com.fiap.mindcare_diary.repositories.PacienteRepository;
import com.fiap.mindcare_diary.repositories.ProfissionalRepository;
import com.fiap.mindcare_diary.models.dtos.ConsultaDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgendamentoServiceTest {
    @Mock ProfissionalRepository profissionalRepository;
    @Mock AgendamentoRepository agendamentoRepository;
    @Mock PacienteRepository pacienteRepository;
    @Mock PushNotificationService pushNotificationService;
    @InjectMocks AgendamentoService service;

    @Test
    void deveRejeitarAgendamentoNoFimDeSemana() {
        ConsultaDTO dto = mock(ConsultaDTO.class);
        when(dto.getDataHoraConsulta()).thenReturn("2026-08-29T10:00");

        assertThrows(RuntimeException.class, () -> service.salvarAgendamento(dto));
        verifyNoInteractions(pacienteRepository, profissionalRepository, agendamentoRepository);
    }

    @Test
    void deveRejeitarAgendamentoNoPassado() {
        ConsultaDTO dto = mock(ConsultaDTO.class);
        when(dto.getDataHoraConsulta()).thenReturn("2020-08-10T10:00");

        assertThrows(RuntimeException.class, () -> service.salvarAgendamento(dto));
        verifyNoInteractions(pacienteRepository, profissionalRepository, agendamentoRepository);
    }

    @Test
    void deveRejeitarAgendamentoForaDoHorarioComercial() {
        ConsultaDTO dto = mock(ConsultaDTO.class);
        when(dto.getDataHoraConsulta()).thenReturn("2026-08-31T19:00");

        assertThrows(RuntimeException.class, () -> service.salvarAgendamento(dto));
        verifyNoInteractions(pacienteRepository, profissionalRepository, agendamentoRepository);
    }

    @Test
    void deveLancarExcecaoQuandoPacienteNaoExistir() {
        ConsultaDTO dto = new ConsultaDTO();
        PacienteDTO pacienteDto = new PacienteDTO();
        dto.setDataHoraConsulta("2026-11-30T10:00");
        dto.setPaciente(pacienteDto);
        pacienteDto.setNomeUsuario("pac");
        when(pacienteRepository.findByNomeUsuario("pac")).thenReturn(Optional.empty());

        assertThrows(PacienteNaoEncontradoException.class, () -> service.salvarAgendamento(dto));
    }

    @Test
    void deveLancarExcecaoQuandoProfissionalNaoExistir() {
        ConsultaDTO dto = new ConsultaDTO();
        PacienteDTO pacienteDto = new PacienteDTO();
        ProfissionalDTO profissionalDto = new ProfissionalDTO();
        dto.setDataHoraConsulta("2026-11-30T10:00");
        dto.setPaciente(pacienteDto);
        dto.setProfissional(profissionalDto);
        pacienteDto.setNomeUsuario("pac");
        profissionalDto.setNomeUsuario("prof");
        Paciente paciente = new Paciente();
        paciente.setNomeUsuario("pac");

        when(pacienteRepository.findByNomeUsuario("pac")).thenReturn(Optional.of(new Paciente()));
        when(profissionalRepository.findByNomeUsuario("prof")).thenReturn(Optional.empty());

        assertThrows(ProfissionalNaoEncontradoException.class, () -> service.salvarAgendamento(dto));
    }
}
