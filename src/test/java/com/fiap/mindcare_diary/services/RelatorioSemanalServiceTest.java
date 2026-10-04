package com.fiap.mindcare_diary.services;

import com.fiap.mindcare_diary.models.Paciente;
import com.fiap.mindcare_diary.models.RegistroDiario;
import com.fiap.mindcare_diary.models.RelatorioSemanal;
import com.fiap.mindcare_diary.repositories.PacienteRepository;
import com.fiap.mindcare_diary.repositories.RegistroDiarioRepository;
import com.fiap.mindcare_diary.repositories.RelatorioSemanalRepository;
import com.fiap.mindcare_diary.utils.DataLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RelatorioSemanalServiceTest {

    @Mock RelatorioSemanalRepository relatorioSemanalRepository;
    @Mock RegistroDiarioRepository registroDiarioRepository;
    @Mock PacienteRepository pacienteRepository;
    @Mock ChatClient.Builder chatClientBuilder;
    @Mock ChatClient chatClient;
    @Mock ChatClient.ChatClientRequestSpec chatClientRequestSpec;
    @Mock ChatClient.CallResponseSpec callResponseSpec;
    @Mock VectorStore vectorStore;
    @Mock DataLoader dataLoader;

    private RelatorioSemanalService service;

    @BeforeEach
    public void setup(){

        when(chatClientBuilder.build())
                .thenReturn(chatClient);

        service = new RelatorioSemanalService(
                relatorioSemanalRepository,
                registroDiarioRepository,
                pacienteRepository,
                chatClientBuilder,
                vectorStore,
                dataLoader
        );
    }

    @Test
    void deveLancarExcecaoQuandoPacienteNaoExistir() {

        when(chatClientBuilder.build()).thenReturn(chatClient);
        when(pacienteRepository.findByNomeUsuario("x")).thenReturn(Optional.empty());

        RelatorioSemanalService service = new RelatorioSemanalService(
                relatorioSemanalRepository, registroDiarioRepository,
                pacienteRepository, chatClientBuilder, vectorStore, dataLoader);



        assertThrows(RuntimeException.class, () -> service.gerarRelatorioSemanal("x"));
        verify(relatorioSemanalRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoAtualizarRelatorioDePacienteInexistente() {

        when(chatClientBuilder.build()).thenReturn(chatClient);
        when(pacienteRepository.findByNomeUsuario("x")).thenReturn(Optional.empty());

        RelatorioSemanalService service = new RelatorioSemanalService(
                relatorioSemanalRepository, registroDiarioRepository,
                pacienteRepository, chatClientBuilder, vectorStore, dataLoader);

        var dto = mock(com.fiap.mindcare_diary.models.dtos.RelatorioSemanalDTO.class);
        var pacienteDto = mock(com.fiap.mindcare_diary.models.dtos.PacienteDTO.class);
        when(dto.getPaciente()).thenReturn(pacienteDto);
        when(pacienteDto.getNomeUsuario()).thenReturn("x");

        assertThrows(RuntimeException.class, () -> service.atualizarRelatorioSemanal(dto));
    }
    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.ValueSource(ints = {7, 9})
    public void incluiDiariosEChatsSemLimitarASeteRegistros(int total) {
        var paciente = new Paciente();
        paciente.setNomeUsuario("p");
        when(pacienteRepository.findByNomeUsuario("p")).thenReturn(Optional.of(paciente));
        List<RegistroDiario> registros = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            var r = registro(i + 1, java.time.LocalDateTime.now().minusHours(i * 16L).minusMinutes(1));
            if (i < 2) {
                r.setPontosPositivos("positivo-" + i);
                r.setDificuldadesDesafios("dificuldade-" + i);
            } else {
                r.setOrigem(com.fiap.mindcare_diary.models.enums.OrigemRegistro.CHAT);
                r.setTextoConfirmado("relato-chat-" + i);
            }
            r.setPaciente(paciente);
            registros.add(r);
        }
        var antigo = registro(90, java.time.LocalDateTime.now().minusDays(8));
        antigo.setTextoConfirmado("NAO-INCLUIR-ANTIGO");
        antigo.setPaciente(paciente);
        registros.add(antigo);
        var futuro = registro(91, java.time.LocalDateTime.now().plusDays(1));
        futuro.setTextoConfirmado("NAO-INCLUIR-FUTURO");
        futuro.setPaciente(paciente);
        registros.add(futuro);
        when(registroDiarioRepository.carregarUltimosRegistrosDiarios(anyString())).thenReturn(registros);
        when(vectorStore.similaritySearch(anyString())).thenReturn(carregarDocumentos(registros, Optional.of(paciente)));

        when(chatClient.prompt(any(Prompt.class)))
                .thenReturn(chatClientRequestSpec);

        when(chatClientRequestSpec.user(anyString()))
                .thenReturn(chatClientRequestSpec);

        when(chatClientRequestSpec.call())
                .thenReturn(callResponseSpec);

        when(callResponseSpec.content())
                .thenReturn("Resposta da IA");

        service.gerarRelatorioSemanal("p");

        verify(relatorioSemanalRepository, times(1)).save(any(RelatorioSemanal.class));
    }

    @Test
    void semRegistrosNaoChamaIA() {
        var paciente = new Paciente();
        paciente.setNomeUsuario("p");
        when(pacienteRepository.findByNomeUsuario("p")).thenReturn(Optional.of(paciente));
        when(registroDiarioRepository.carregarUltimosRegistrosDiarios(paciente.getNomeUsuario())).thenReturn(List.of());
        var dto = service.gerarRelatorioSemanal("p");
        assertEquals(true, dto.getRelatorioIA().isBlank());
        verifyNoInteractions(chatClient, vectorStore, dataLoader);
    }

    private com.fiap.mindcare_diary.models.RegistroDiario registro(long id, java.time.LocalDateTime data) {
        var r = new com.fiap.mindcare_diary.models.RegistroDiario();
        r.setId(id);
        r.setDataHoraCriacao(data);
        r.setNivelHumor(com.fiap.mindcare_diary.models.enums.NivelHumor.SEM_DEFINICAO);
        return r;
    }

    private List<Document> carregarDocumentos(List<RegistroDiario> ultimosRegistros, Optional<Paciente> optionalPaciente) {
        List<Document> documents = new ArrayList<>();
        ultimosRegistros.forEach(registro -> {
            String nivelHumor = registro.getNivelHumor().name();
            Long id = registro.getId();
            String text = "";
            if(registro.getTextoConfirmado() != null) {
                if(registro.getTextoConfirmado().isBlank()) {
                    text = "Paciente " + optionalPaciente.get().getNomeCompleto() +
                            " descreveu suas dificuldades como '" + registro.getDificuldadesDesafios() + "', \n" +
                            "seus pontos positivos como '" + registro.getPontosPositivos() + "'.";
                } else {
                    text = "Paciente " + optionalPaciente.get().getNomeCompleto() +
                            " escreveu : '" + registro.getTextoConfirmado() + "'.";
                }
            } else {
                text = "";
            }
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("id", id);
            metadata.put("nivelHumor", nivelHumor);
            documents.add(new Document(text, metadata));
        });
        return documents;
    }
}