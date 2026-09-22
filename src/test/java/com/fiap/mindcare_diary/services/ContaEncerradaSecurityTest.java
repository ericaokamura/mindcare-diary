package com.fiap.mindcare_diary.services;
import com.fiap.mindcare_diary.configuration.SecurityFilter;
import com.fiap.mindcare_diary.controllers.LoginController;
import com.fiap.mindcare_diary.models.*;
import com.fiap.mindcare_diary.repositories.UsuarioRepository;
import org.junit.jupiter.api.*;
import org.springframework.mock.web.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class ContaEncerradaSecurityTest {
    @AfterEach void limpar() { SecurityContextHolder.clearContext(); }
    @Test void jwtAnteriorNaoAutenticaContaEncerrada() throws Exception {
        var usuario = new Usuario();
        usuario.setEncerradaEm(LocalDateTime.now());
        var repo = mock(UsuarioRepository.class);
        var tokens = mock(TokenService.class);
        when(repo.findByNomeUsuario("usuario")).thenReturn(Optional.of(usuario));
        when(tokens.getSubject("antigo")).thenReturn("usuario");
        when(tokens.getExpirationDate("antigo")).thenReturn(new Date(System.currentTimeMillis() + 60000));
        var filter = new SecurityFilter();
        ReflectionTestUtils.setField(filter, "usuarioRepository", repo);
        ReflectionTestUtils.setField(filter, "tokenService", tokens);
        var request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer antigo");
        filter.doFilter(request, new MockHttpServletResponse(), new MockFilterChain());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
    @Test void loginRecusaContaEncerradaAntesDeGerarToken() {
        var usuario = new Usuario();
        usuario.setEncerradaEm(LocalDateTime.now());
        var repo = mock(UsuarioRepository.class);
        when(repo.findByNomeUsuario("usuario")).thenReturn(Optional.of(usuario));
        var tokens = mock(TokenService.class);
        var controller = new LoginController();
        ReflectionTestUtils.setField(controller, "usuarioRepository", repo);
        ReflectionTestUtils.setField(controller, "tokenService", tokens);
        assertEquals(403, controller.efetuarLogin(new DadosAutenticacao("usuario", "senha", PrivacidadeService.VERSAO)).getStatusCode().value());
        verifyNoInteractions(tokens);
    }
}

