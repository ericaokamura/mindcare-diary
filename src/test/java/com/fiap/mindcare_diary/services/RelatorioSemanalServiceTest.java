package com.fiap.mindcare_diary.services;

import com.fiap.mindcare_diary.models.Paciente;
import com.fiap.mindcare_diary.repositories.PacienteRepository;
import com.fiap.mindcare_diary.repositories.RegistroDiarioRepository;
import com.fiap.mindcare_diary.repositories.RelatorioSemanalRepository;
import com.fiap.mindcare_diary.utils.DataLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RelatorioSemanalServiceTest {
    @Mock RelatorioSemanalRepository relatorioSemanalRepository;
    @Mock RegistroDiarioRepository registroDiarioRepository;
    @Mock PacienteRepository pacienteRepository;
    @Mock ChatClient.Builder chatClientBuilder;
    @Mock ChatClient chatClient;
    @Mock VectorStore vectorStore;
    @Mock DataLoader dataLoader;

    @Test
    void deveLancarExcecaoQuandoPacienteNaoExistir() {

        when(chatClientBuilder.build()).thenReturn(chatClient);
        when(pacienteRepository.findByNomeUsuario("x")).thenReturn(Optional.empty());

        RelatorioSemanalService service = new RelatorioSemanalService(
                relatorioSemanalRepository, registroDiarioRepository,
                pacienteRepository, chatClientBuilder);



        assertThrows(RuntimeException.class, () -> service.gerarRelatorioSemanal("x"));
        verify(relatorioSemanalRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoAtualizarRelatorioDePacienteInexistente() {

        when(chatClientBuilder.build()).thenReturn(chatClient);
        when(pacienteRepository.findByNomeUsuario("x")).thenReturn(Optional.empty());

        RelatorioSemanalService service = new RelatorioSemanalService(
                relatorioSemanalRepository, registroDiarioRepository,
                pacienteRepository, chatClientBuilder);

        var dto = mock(com.fiap.mindcare_diary.models.dtos.RelatorioSemanalDTO.class);
        var pacienteDto = mock(com.fiap.mindcare_diary.models.dtos.PacienteDTO.class);
        when(dto.getPaciente()).thenReturn(pacienteDto);
        when(pacienteDto.getNomeUsuario()).thenReturn("x");

        assertThrows(RuntimeException.class, () -> service.atualizarRelatorioSemanal(dto));
    }
    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.ValueSource(ints = {7, 9})
    void incluiDiariosEChatsSemLimitarASeteRegistros(int total) {
        var paciente = new Paciente();
        when(pacienteRepository.findByNomeUsuario("p")).thenReturn(Optional.of(paciente));
        var registros = new java.util.ArrayList<com.fiap.mindcare_diary.models.RegistroDiario>();
        for (int i = 0; i < total; i++) {
            var r = registro(i + 1, java.time.LocalDateTime.now().minusHours(i * 16L).minusMinutes(1));
            if (i < 2) {
                r.setPontosPositivos("positivo-" + i);
                r.setDificuldadesDesafios("dificuldade-" + i);
            } else {
                r.setOrigem(com.fiap.mindcare_diary.models.enums.OrigemRegistro.CHAT);
                r.setTextoConfirmado("relato-chat-" + i);
            }
            registros.add(r);
        }
        var antigo = registro(90, java.time.LocalDateTime.now().minusDays(8));
        antigo.setTextoConfirmado("NAO-INCLUIR-ANTIGO");
        registros.add(antigo);
        var futuro = registro(91, java.time.LocalDateTime.now().plusDays(1));
        futuro.setTextoConfirmado("NAO-INCLUIR-FUTURO");
        registros.add(futuro);
        when(registroDiarioRepository.findAllByPaciente(paciente)).thenReturn(registros);
        var request = mock(ChatClient.ChatClientRequestSpec.class, RETURNS_SELF);
        var response = mock(ChatClient.CallResponseSpec.class);
        when(chatClientBuilder.build()).thenReturn(chatClient);
        when(chatClient.prompt()).thenReturn(request);
        when(request.call()).thenReturn(response);
        when(response.content()).thenReturn("Resumo conjunto");
        var service = new RelatorioSemanalService(relatorioSemanalRepository, registroDiarioRepository, pacienteRepository, chatClientBuilder);
        var dto = service.gerarRelatorioSemanal("p");
        var contexto = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(request).user(contexto.capture());
        String texto = contexto.getValue();
        for (int i = 0; i < 2; i++) {
            assertTrue(texto.contains("positivo-" + i));
            assertTrue(texto.contains("dificuldade-" + i));
        }
        for (int i = 2; i < total; i++) assertTrue(texto.contains("relato-chat-" + i));
        assertFalse(texto.contains("NAO-INCLUIR"));
        assertTrue(texto.indexOf("relato-chat-6") < texto.indexOf("positivo-0"));
        assertEquals(total, dto.getRegistrosDiarios().size());
        assertEquals("Resumo conjunto", dto.getRelatorioIA());
        var salvo = org.mockito.ArgumentCaptor.forClass(com.fiap.mindcare_diary.models.RelatorioSemanal.class);
        verify(relatorioSemanalRepository).save(salvo.capture());
        assertEquals(total, salvo.getValue().getRegistrosDiarios().size());
        verifyNoInteractions(vectorStore, dataLoader);
    }

    @Test
    void semRegistrosNaoChamaIA() {
        var paciente = new Paciente();
        when(pacienteRepository.findByNomeUsuario("p")).thenReturn(Optional.of(paciente));
        when(registroDiarioRepository.findAllByPaciente(paciente)).thenReturn(java.util.List.of());
        when(chatClientBuilder.build()).thenReturn(chatClient);
        var service = new RelatorioSemanalService(relatorioSemanalRepository, registroDiarioRepository, pacienteRepository, chatClientBuilder);
        var dto = service.gerarRelatorioSemanal("p");
        assertTrue(dto.getRelatorioIA().contains("Não há registros"));
        verifyNoInteractions(chatClient, vectorStore, dataLoader);
    }

    private com.fiap.mindcare_diary.models.RegistroDiario registro(long id, java.time.LocalDateTime data) {
        var r = new com.fiap.mindcare_diary.models.RegistroDiario();
        r.setId(id);
        r.setDataHoraCriacao(data);
        r.setNivelHumor(com.fiap.mindcare_diary.models.enums.NivelHumor.SEM_DEFINICAO);
        return r;
    }
}