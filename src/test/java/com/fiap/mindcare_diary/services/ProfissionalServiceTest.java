package com.fiap.mindcare_diary.services;

import com.fiap.mindcare_diary.models.Profissional;
import com.fiap.mindcare_diary.models.dtos.ProfissionalDTO;
import com.fiap.mindcare_diary.repositories.PacienteRepository;
import com.fiap.mindcare_diary.repositories.ProfissionalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfissionalServiceTest {
    @Mock ProfissionalRepository profissionalRepository;
    @Mock PacienteRepository pacienteRepository;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks ProfissionalService service;

    @Test
    void deveLancarExcecaoAoCadastrarProfissionalDuplicado() {
        Profissional existente = new Profissional();
        when(profissionalRepository.findByNomeUsuario("prof")).thenReturn(Optional.of(existente));

        ProfissionalDTO dto = mock(ProfissionalDTO.class);
        when(dto.getNomeUsuario()).thenReturn("prof");

        assertThrows(RuntimeException.class, () -> service.salvarCadastroProfissional(dto));
        verify(profissionalRepository, never()).save(any());
    }

    @Test
    void deveRetornarListaDeProfissionais() {
        Profissional profissional = new Profissional();
        profissional.setNomeUsuario("prof");
        when(profissionalRepository.findAll()).thenReturn(List.of(profissional));

        assertEquals(1, service.retornarProfissionais().size());
    }

    @Test
    void deveLancarExcecaoAoBuscarProfissionalInexistente() {
        when(profissionalRepository.findByNomeUsuario("x")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.retornarProfissional("x"));
    }

    @Test
    void deveFiltrarProfissionaisPorTipo() {
        Profissional p = new Profissional();
        p.setTipoProfissional(com.fiap.mindcare_diary.models.enums.TipoProfissional.PSICOLOGO);
        when(profissionalRepository.findAll()).thenReturn(List.of(p));

        assertEquals(1, service.buscarProfissionaisPorTipo("PSICOLOGO").size());
        assertTrue(service.buscarProfissionaisPorTipo("PSIQUIATRA").isEmpty());
    }
}
