package com.fiap.mindcare_diary.services;

import com.fiap.mindcare_diary.models.Paciente;
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
        ConsultaDTO dto = mock(ConsultaDTO.class);
        var pacienteDto = mock(com.fiap.mindcare_diary.models.dtos.PacienteDTO.class);
        when(dto.getDataHoraConsulta()).thenReturn("2026-08-31T10:00");
        when(dto.getPaciente()).thenReturn(pacienteDto);
        when(pacienteDto.getNomeUsuario()).thenReturn("pac");
        when(pacienteRepository.findByNomeUsuario("pac")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.salvarAgendamento(dto));
    }

    @Test
    void deveLancarExcecaoQuandoProfissionalNaoExistir() {
        ConsultaDTO dto = mock(ConsultaDTO.class);
        var pacienteDto = mock(com.fiap.mindcare_diary.models.dtos.PacienteDTO.class);
        var profissionalDto = mock(com.fiap.mindcare_diary.models.dtos.ProfissionalDTO.class);
        when(dto.getDataHoraConsulta()).thenReturn("2026-08-31T10:00");
        when(dto.getPaciente()).thenReturn(pacienteDto);
        when(dto.getProfissional()).thenReturn(profissionalDto);
        when(pacienteDto.getNomeUsuario()).thenReturn("pac");
        when(profissionalDto.getNomeUsuario()).thenReturn("prof");
        when(pacienteRepository.findByNomeUsuario("pac")).thenReturn(Optional.of(new Paciente()));
        when(profissionalRepository.findByNomeUsuario("prof")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.salvarAgendamento(dto));
    }
}
