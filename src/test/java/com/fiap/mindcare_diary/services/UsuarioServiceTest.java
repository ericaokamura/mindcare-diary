package com.fiap.mindcare_diary.services;

import com.fiap.mindcare_diary.models.Usuario;
import com.fiap.mindcare_diary.models.enums.Sexo;
import com.fiap.mindcare_diary.models.enums.UserRole;
import com.fiap.mindcare_diary.repositories.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {
    @Mock UsuarioRepository usuarioRepository;
    @InjectMocks UsuarioService service;

    @Test
    void deveRetornarTodosUsuarios() throws NullPointerException {
        Usuario usuario = new Usuario();
        usuario.setNomeUsuario("erica");
        usuario.setDataNascimento(LocalDate.of(2000, 1, 1));
        usuario.setDataHoraAtivacao(LocalDateTime.now());
        usuario.setGenero(Sexo.FEMININO);
        usuario.setUserRole(UserRole.PACIENTE);
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        var result = service.retornarTodosUsuarios();

        assertEquals(1, result.size());
        assertEquals("erica", result.get(0).getNomeUsuario());
    }

    @Test
    void deveSalvarTokenDoUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNomeUsuario("erica");
        when(usuarioRepository.findByNomeUsuario("erica")).thenReturn(Optional.of(usuario));

        service.salvarToken("erica", "token-123");

        assertEquals("token-123", usuario.getToken());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deveLancarExcecaoAoSalvarTokenDeUsuarioInexistente() {
        when(usuarioRepository.findByNomeUsuario("x")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.salvarToken("x", "token"));
        verify(usuarioRepository, never()).save(any());
    }
}
