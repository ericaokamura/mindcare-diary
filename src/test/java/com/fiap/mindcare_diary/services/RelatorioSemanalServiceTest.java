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
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;

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
    @Mock PgVectorStore pgVectorStore;
    @Mock DataLoader dataLoader;

    @Test
    void deveLancarExcecaoQuandoPacienteNaoExistir() {
        when(chatClientBuilder.defaultAdvisors(any(Advisor.class))).thenReturn(chatClientBuilder);
        when(chatClientBuilder.build()).thenReturn(chatClient);
        when(pacienteRepository.findByNomeUsuario("x")).thenReturn(Optional.empty());

        RelatorioSemanalService service = new RelatorioSemanalService(
                relatorioSemanalRepository, registroDiarioRepository,
                pacienteRepository, chatClientBuilder, pgVectorStore, dataLoader);

        // A geração da IA ocorre antes da busca do paciente; portanto o teste
        // isola esse caminho fazendo o vetor retornar nenhum documento.
        when(pgVectorStore.similaritySearch(anyString())).thenReturn(java.util.List.of());

        assertThrows(RuntimeException.class, () -> service.gerarRelatorioSemanal("x"));
        verify(relatorioSemanalRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoAtualizarRelatorioDePacienteInexistente() {
        when(chatClientBuilder.defaultAdvisors(any(Advisor.class))).thenReturn(chatClientBuilder);
        when(chatClientBuilder.build()).thenReturn(chatClient);
        when(pacienteRepository.findByNomeUsuario("x")).thenReturn(Optional.empty());

        RelatorioSemanalService service = new RelatorioSemanalService(
                relatorioSemanalRepository, registroDiarioRepository,
                pacienteRepository, chatClientBuilder, pgVectorStore, dataLoader);

        var dto = mock(com.fiap.mindcare_diary.models.dtos.RelatorioSemanalDTO.class);
        var pacienteDto = mock(com.fiap.mindcare_diary.models.dtos.PacienteDTO.class);
        when(dto.getPaciente()).thenReturn(pacienteDto);
        when(pacienteDto.getNomeUsuario()).thenReturn("x");

        assertThrows(RuntimeException.class, () -> service.atualizarRelatorioSemanal(dto));
    }
}
