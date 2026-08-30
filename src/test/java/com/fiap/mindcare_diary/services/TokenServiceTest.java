package com.fiap.mindcare_diary.services;

import com.fiap.mindcare_diary.models.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {
    private TokenService service;

    @BeforeEach
    void setUp() {
        service = new TokenService();
        ReflectionTestUtils.setField(service, "secret", "test-secret-123456789");
    }

    @Test
    void deveGerarTokenComSubjectDoUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNomeUsuario("erica");

        String token = service.gerarToken(usuario);

        assertNotNull(token);
        assertEquals("erica", service.getSubject(token));
    }

    @Test
    void deveRetornarDataDeExpiracaoFutura() {
        Usuario usuario = new Usuario();
        usuario.setNomeUsuario("erica");

        String token = service.gerarToken(usuario);
        Date expiration = service.getExpirationDate(token);

        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void deveRejeitarTokenInvalido() {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.getSubject("token-invalido"));

        assertEquals("Token JWT inválido ou expirado!", exception.getMessage());
    }
}
