package com.fiap.mindcare_diary.services;

import com.fiap.mindcare_diary.models.Usuario;
import com.fiap.mindcare_diary.repositories.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutenticacaoServiceTest {
    @Mock UsuarioRepository usuarioRepository;
    @InjectMocks AutenticacaoService service;

    @Test
    void deveRetornarUsuarioQuandoUsernameExistir() {
        Usuario usuario = new Usuario();
        usuario.setNomeUsuario("erica");
        when(usuarioRepository.findByNomeUsuario("erica")).thenReturn(Optional.of(usuario));

        assertSame(usuario, service.loadUserByUsername("erica"));
        verify(usuarioRepository).findByNomeUsuario("erica");
    }

    @Test
    void deveLancarExcecaoQuandoUsernameNaoExistir() {
        when(usuarioRepository.findByNomeUsuario("inexistente")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("inexistente"));
    }
}
