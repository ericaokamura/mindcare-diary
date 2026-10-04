package com.fiap.mindcare_diary.services.mia;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.mindcare_diary.models.Usuario;
import com.fiap.mindcare_diary.models.dtos.MiaMessageRequest;
import com.fiap.mindcare_diary.models.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MiaServiceTest {

    @Test
    void diagnosticLogsIdentifyFailuresWithoutSensitiveContent() {
        var logger = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(MiaService.class);
        var appender = new ch.qos.logback.core.read.ListAppender<ch.qos.logback.classic.spi.ILoggingEvent>();
        appender.start(); logger.addAppender(appender);
        try {
            when(ai.generate(anyString())).thenReturn("conteudo privado")
                    .thenThrow(new RuntimeException("chave secreta"));
            service.reply(auth, new MiaMessageRequest("Hoje caminhei"));
            service.reply(auth, new MiaMessageRequest("Hoje caminhei"));
            assertEquals(2, appender.list.size());
            assertTrue(appender.list.get(0).getFormattedMessage().contains("RESPOSTA_INVALIDA"));
            assertTrue(appender.list.get(1).getFormattedMessage().contains("FALHA_PROVEDOR"));
            for (var event : appender.list) {
                assertNull(event.getThrowableProxy());
                assertFalse(event.getFormattedMessage().contains("conteudo privado"));
                assertFalse(event.getFormattedMessage().contains("chave secreta"));
                assertFalse(event.getFormattedMessage().contains("Hoje caminhei"));
            }
        } finally { logger.detachAppender(appender); appender.stop(); }
    }

    private MiaAiService ai;
    private MiaService service;
    private Usuario user;
    private Authentication auth;

    @BeforeEach
    void setUp() {
        ai = mock(MiaAiService.class);
        service = new MiaService(new MiaIntentClassifier(), ai, new MiaResponseValidator(new ObjectMapper()));
        user = new Usuario();
        user.setUserRole(UserRole.PACIENTE);
        user.setAtivo(true);
        auth = UsernamePasswordAuthenticationToken.authenticated(Optional.of(user), null, List.of());
    }

    @Test
    void returnsSafeReplyWithoutCallingProvider() {
        var response = service.reply(auth, new MiaMessageRequest("Qual remédio devo tomar?"));
        assertTrue(response.mensagem().contains("Não posso recomendar medicamentos"));
        assertFalse(response.fallback());
        verifyNoInteractions(ai);
    }

    @Test
    void normalMessageUsesSemanticClassificationAndValidatedQuestion() {
        when(ai.generate("Hoje foi um dia bom")).thenReturn("{\"intencao\":\"REGISTRO_NORMAL\",\"pergunta\":\"ADICIONAR\"}");
        var response = service.reply(auth, new MiaMessageRequest(" Hoje foi um dia bom "));
        assertEquals(MiaSafeResponses.Question.ADICIONAR.text(), response.mensagem());
        assertEquals("Assistente de registro", response.papel());
        assertFalse(response.fallback());
        verify(ai).generate("Hoje foi um dia bom");
    }

    @Test
    void semanticSensitiveClassificationUsesFixedResponse() {
        when(ai.generate(anyString())).thenReturn("{\"intencao\":\"SITUACAO_SENSIVEL\",\"pergunta\":\"DETALHES\"}");
        var response = service.reply(auth, new MiaMessageRequest("Penso em desaparecer para sempre"));
        assertTrue(response.mensagem().contains("não aciona ajuda automaticamente"));
    }

    @Test
    void invalidResponseAndProviderFailureNeverLeakContent() {
        when(ai.generate(anyString())).thenReturn("Você tem um transtorno")
                .thenThrow(new RuntimeException("conteudo privado token segredo"))
                .thenReturn("{\"intencao\":\"REGISTRO_NORMAL\",\"pergunta\":\"DIA\"}");
        for (int i = 0; i < 2; i++) {
            var response = service.reply(auth, new MiaMessageRequest("Hoje foi difícil"));
            assertTrue(response.fallback());
            assertEquals(MiaSafeResponses.unavailable(), response.mensagem());
        }
        assertFalse(service.reply(auth, new MiaMessageRequest("Hoje foi difícil")).fallback());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\n\t"})
    void rejectsEmptyMessage(String message) {
        assertStatus(HttpStatus.BAD_REQUEST, auth, new MiaMessageRequest(message));
        verifyNoInteractions(ai);
    }

    @Test
    void rejectsMissingAndOversizedMessage() {
        assertStatus(HttpStatus.BAD_REQUEST, auth, null);
        assertStatus(HttpStatus.BAD_REQUEST, auth, new MiaMessageRequest("a".repeat(4001)));
        verifyNoInteractions(ai);
    }

    @Test
    void deniesAnonymousAndUnauthenticatedBeforeProviderCall() {
        assertStatus(HttpStatus.UNAUTHORIZED, null, new MiaMessageRequest("Oi"));
        assertStatus(HttpStatus.UNAUTHORIZED, UsernamePasswordAuthenticationToken.unauthenticated(user, null), new MiaMessageRequest("Oi"));
        assertStatus(HttpStatus.FORBIDDEN, UsernamePasswordAuthenticationToken.authenticated("anonymousUser", null, List.of()), new MiaMessageRequest("Oi"));
        verifyNoInteractions(ai);
    }

    @Test
    void deniesOtherRolesInactiveAndBlockedPatients() {
        for (UserRole role : List.of(UserRole.ADMIN, UserRole.PROFISSIONAL)) {
            user.setUserRole(role);
            assertStatus(HttpStatus.FORBIDDEN, auth, new MiaMessageRequest("Oi"));
        }
        user.setUserRole(UserRole.PACIENTE);
        user.setAtivo(false);
        assertStatus(HttpStatus.FORBIDDEN, auth, new MiaMessageRequest("Oi"));
        user.setAtivo(true);
        user.setBloqueado(true);
        assertStatus(HttpStatus.FORBIDDEN, auth, new MiaMessageRequest("Oi"));
        verifyNoInteractions(ai);
    }

    @Test
    void alsoAcceptsUnwrappedPrincipalAndDoesNotEchoRequestInToString() {
        var plain = UsernamePasswordAuthenticationToken.authenticated(user, null, List.of());
        assertFalse(service.reply(plain, new MiaMessageRequest("me dê um conselho")).fallback());
        assertFalse(new MiaMessageRequest("conteudo privado").toString().contains("conteudo privado"));
    }

    @Test
    void limitsConcurrentProviderCallsWithoutBlockingDeterministicGuardrails() throws Exception {
        var entered = new CountDownLatch(4);
        var release = new CountDownLatch(1);
        when(ai.generate(anyString())).thenAnswer(invocation -> {
            entered.countDown();
            if (!release.await(10, TimeUnit.SECONDS)) throw new IllegalStateException("test timeout");
            return "{\"intencao\":\"REGISTRO_NORMAL\",\"pergunta\":\"DIA\"}";
        });
        var executor = Executors.newFixedThreadPool(4);
        try {
            var futures = IntStream.range(0, 4).mapToObj(i -> executor.submit(
                    () -> service.reply(auth, new MiaMessageRequest("Hoje foi um dia bom")))).toList();
            assertTrue(entered.await(10, TimeUnit.SECONDS));
            assertTrue(service.reply(auth, new MiaMessageRequest("Olá")).fallback());
            var sensitive = service.reply(auth, new MiaMessageRequest("Quero me machucar"));
            assertFalse(sensitive.fallback());
            assertTrue(sensitive.mensagem().contains("emergência"));
            release.countDown();
            for (var future : futures) assertFalse(future.get(10, TimeUnit.SECONDS).fallback());
            assertFalse(service.reply(auth, new MiaMessageRequest("Olá")).fallback());
            verify(ai, times(5)).generate(anyString());
        } finally {
            release.countDown();
            executor.shutdownNow();
        }
    }

    private void assertStatus(HttpStatus expected, Authentication authentication, MiaMessageRequest request) {
        var exception = assertThrows(ResponseStatusException.class, () -> service.reply(authentication, request));
        assertEquals(expected, exception.getStatusCode());
    }
}
