package com.fiap.mindcare_diary.utils;

import com.fiap.mindcare_diary.exceptions.PacienteNaoEncontradoException;
import com.fiap.mindcare_diary.models.*;
import com.fiap.mindcare_diary.repositories.PacienteRepository;
import com.fiap.mindcare_diary.repositories.RegistroDiarioRepository;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

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
        System.out.println("📥 Deleteando dados da tabela SPRING_AI_VECTORS do banco de dados Oracle SQL...");
        jdbcTemplate.execute("DELETE from SPRING_AI_VECTORS");
        System.out.println("📥 Carregando dados a partir da tabela registro_diario do banco de dados Oracle SQL...");
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
        } else {
            throw new PacienteNaoEncontradoException("Paciente não encontrado.");
        }

    }

}
