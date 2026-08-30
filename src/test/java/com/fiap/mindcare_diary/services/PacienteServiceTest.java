package com.fiap.mindcare_diary.services;

import com.fiap.mindcare_diary.models.Paciente;
import com.fiap.mindcare_diary.models.Profissional;
import com.fiap.mindcare_diary.models.enums.TipoProfissional;
import com.fiap.mindcare_diary.repositories.PacienteRepository;
import com.fiap.mindcare_diary.repositories.PrescriptionRepository;
import com.fiap.mindcare_diary.repositories.ProfissionalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PacienteServiceTest {
    @Mock PacienteRepository pacienteRepository;
    @Mock ProfissionalRepository profissionalRepository;
    @Mock PrescriptionRepository prescriptionRepository;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks PacienteService service;

    @Test
    void deveLancarExcecaoQuandoPacienteNaoExistir() {
        when(pacienteRepository.findByNomeUsuario("x")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.retornarCadastroPaciente("x"));
    }

    @Test
    void deveLancarExcecaoQuandoProfissionalNaoExistirAoSelecionar() {
        Paciente paciente = new Paciente();
        when(pacienteRepository.findByNomeUsuario("pac")).thenReturn(Optional.of(paciente));
        when(profissionalRepository.findByNomeUsuario("prof")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.selecionarProfissional("prof", "pac"));
        verify(pacienteRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoParaUploadPorProfissionalNaoPsiquiatra() {
        Profissional profissional = new Profissional();
        profissional.setTipoProfissional(TipoProfissional.PSICOLOGO);
        MultipartFile file = mock(MultipartFile.class);

        assertThrows(RuntimeException.class, () ->
                service.salvarPrescricaoDePaciente(
                        "pac", profissional, "2026-08-01", "2026-09-01",
                        "Medicamento A", false, file));
        verifyNoInteractions(pacienteRepository, prescriptionRepository);
    }

    @Test
    void deveRejeitarUploadSemArquivo() {
        Profissional profissional = new Profissional();
        profissional.setTipoProfissional(TipoProfissional.PSIQUIATRA);

        assertThrows(IllegalArgumentException.class, () ->
                service.salvarPrescricaoDePaciente(
                        "pac", profissional, "2026-08-01", "2026-09-01",
                        "Medicamento A", false, null));
    }

    @Test
    void deveRejeitarArquivoQueNaoSejaPdf() {
        Profissional profissional = new Profissional();
        profissional.setTipoProfissional(TipoProfissional.PSIQUIATRA);
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("image/png");

        assertThrows(IllegalArgumentException.class, () ->
                service.salvarPrescricaoDePaciente(
                        "pac", profissional, "2026-08-01", "2026-09-01",
                        "Medicamento A", false, file));
    }
}
