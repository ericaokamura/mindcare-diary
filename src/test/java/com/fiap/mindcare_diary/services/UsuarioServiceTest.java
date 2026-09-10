package com.fiap.mindcare_diary.services;

import com.fiap.mindcare_diary.exceptions.UsuarioJaExistenteException;
import com.fiap.mindcare_diary.models.Usuario;
import com.fiap.mindcare_diary.models.dtos.UsuarioDTO;
import com.fiap.mindcare_diary.models.enums.Sexo;
import com.fiap.mindcare_diary.models.enums.UserRole;
import com.fiap.mindcare_diary.repositories.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {
    @Mock UsuarioRepository usuarioRepository;
    @Mock PasswordEncoder passwordEncoder;
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

    @Test
    void deveCadastrarAdminComSenhaHasheadaEUserRoleAdmin() {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNomeUsuario("novoAdmin");
        dto.setSenha("senha123");
        dto.setNomeCompleto("Novo Admin");
        dto.setDataNascimento("2000-01-01");
        dto.setGenero("FEMININO");
        dto.setUserRole("PACIENTE");
        when(usuarioRepository.findByNomeUsuario("novoAdmin")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("senha123")).thenReturn("senha-hash");

        service.salvarCadastroAdmin(dto);

        verify(usuarioRepository).save(argThat(usuario ->
                usuario.getUserRole() == UserRole.ADMIN && "senha-hash".equals(usuario.getSenha())
        ));
    }

    @Test
    void deveLancarExcecaoAoCadastrarAdminComNomeUsuarioJaExistente() {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNomeUsuario("existente");
        when(usuarioRepository.findByNomeUsuario("existente")).thenReturn(Optional.of(new Usuario()));

        assertThrows(UsuarioJaExistenteException.class, () -> service.salvarCadastroAdmin(dto));
        verify(usuarioRepository, never()).save(any());
    }
}
