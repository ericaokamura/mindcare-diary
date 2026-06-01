package com.fiap.mindcare_diary.utils;

import com.fiap.mindcare_diary.exceptions.PacienteNaoEncontradoException;
import com.fiap.mindcare_diary.models.*;
import com.fiap.mindcare_diary.repositories.AgendamentoRepository;
import com.fiap.mindcare_diary.repositories.PacienteRepository;
import com.fiap.mindcare_diary.repositories.RegistroDiarioRepository;
import com.fiap.mindcare_diary.repositories.RelatorioSemanalRepository;
import com.fiap.mindcare_diary.services.AgendamentoService;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class DataLoader {

    @Autowired
    private VectorStore vectorStore;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private RegistroDiarioRepository registroDiarioRepository;
    @Autowired
    private PacienteRepository pacienteRepository;

    public void loadRelatoriosSemanaisIntoVectorStore(String nomeUsuario) {
        System.out.println("📥 Deleteando dados da tabela vector_store do banco de dados PostgreSQL...");
        jdbcTemplate.execute("DELETE from vector_store");
        System.out.println("📥 Carregando dados a partir da tabela consulta do banco de dados PostgreSQL...");
        List<Document> relatoriosSemanais = carregarRegistrosDiarios(nomeUsuario);
        vectorStore.add(relatoriosSemanais);
        System.out.println("✅ Dados de estoque carregados em vector store.");
    }

    private List<Document> carregarRegistrosDiarios(String nomeUsuario) {
        List<Document> documents = new ArrayList<>();
        Optional<Paciente> optionalPaciente = this.pacienteRepository.findByNomeUsuario(nomeUsuario);
        if(optionalPaciente.isPresent()) {
            List<RegistroDiario> registroDiarios = registroDiarioRepository.findAllByPaciente(optionalPaciente.get());
            List<RegistroDiario> ultimosRegistros = registroDiarios.stream()
                    .filter(registro -> registro.getDataHoraCriacao().isAfter(LocalDateTime.now().minusDays(7)))
                    .toList();
            ultimosRegistros.forEach(registro -> {
                String nivelHumor = registro.getNivelHumor().name();
                Long id = registro.getId();

                String text = "Paciente " + optionalPaciente.get().getNomeCompleto() +
                        " descreveu suas dificuldades como '" + registro.getDificuldadesDesafios() + "' e \n" +
                    "seus pontos positivos como '" + registro.getPontosPositivos() + "'.";
                Map<String, Object> metadata = new HashMap<>();
                metadata.put("id", id);
                metadata.put("nivelHumor", nivelHumor);
                documents.add(new Document(text, metadata));
            });
            return documents;
        } else {
            throw new PacienteNaoEncontradoException("Paciente não encontrado.");
        }

    }

}
