package com.fiap.mindcare_diary.services;

import com.fiap.mindcare_diary.models.Clinica;
import com.fiap.mindcare_diary.models.Consulta;
import com.fiap.mindcare_diary.models.enums.PlanoAssinatura;
import com.fiap.mindcare_diary.repositories.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClinicaServiceTest {
    @Mock ClinicaRepository clinicaRepository;
    @Mock ProfissionalRepository profissionalRepository;
    @Mock PacienteRepository pacienteRepository;
    @Mock ConsultaRepository consultaRepository;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks ClinicaService service;

    @Test
    void deveLancarExcecaoQuandoClinicaNaoExistir() {
        when(clinicaRepository.findByCnpj("123")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.retornarClinicaPorCnpj("123"));
        assertThrows(RuntimeException.class, () -> service.retornarProfissionaisPorClinicaCnpj("123"));
        assertThrows(RuntimeException.class, () -> service.retornarPacientesPorClinicaCnpj("123"));
    }

    @Test
    void deveCalcularFaturamentoSomenteDeConsultasAtendidasNaoCanceladasDoMes() {
        Clinica clinica = new Clinica();
        Consulta atendida = new Consulta();
        atendida.setDataHoraConsulta(LocalDateTime.of(2026, 8, 10, 10, 0));
        atendida.setAtendida(true);
        atendida.setCancelada(false);
        atendida.setValorConsulta(200.0);

        Consulta cancelada = new Consulta();
        cancelada.setDataHoraConsulta(LocalDateTime.of(2026, 8, 11, 10, 0));
        cancelada.setAtendida(true);
        cancelada.setCancelada(true);
        cancelada.setValorConsulta(500.0);

        clinica.setConsultas(List.of(atendida, cancelada));
        when(clinicaRepository.findByCnpj("123")).thenReturn(Optional.of(clinica));

        assertEquals(200.0,
                service.retornarFaturamentoPorClinicaCnpjPorAnoMes("123", 2026L, 8L));
    }

    @Test
    void deveCalcularReceitaLiquidaAplicandoTaxaDeComissao() {
        Clinica clinica = new Clinica();
        clinica.setTaxaComissao(0.10);
        Consulta consulta = new Consulta();
        consulta.setDataHoraConsulta(LocalDateTime.of(2026, 8, 10, 10, 0));
        consulta.setAtendida(true);
        consulta.setCancelada(false);
        consulta.setValorConsulta(1000.0);
        clinica.setConsultas(List.of(consulta));
        when(clinicaRepository.findByCnpj("123")).thenReturn(Optional.of(clinica));

        assertEquals(900.0,
                service.retornarReceitaAposDescontosPorClinicaCnpjPorAnoMes("123", 2026L, 8L));
    }

    @Test
    void deveRetornarClinicas() {
        Clinica clinica = new Clinica();
        clinica.setNome("Clínica A");
        clinica.setPlanoAssinatura(PlanoAssinatura.CLINICA);
        when(clinicaRepository.findAll()).thenReturn(List.of(clinica));

        assertEquals(1, service.retornarClinicas().size());
    }
}
