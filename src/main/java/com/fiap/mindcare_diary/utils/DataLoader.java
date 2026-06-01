package com.fiap.mindcare_diary.utils;

import com.fiap.mindcare_diary.models.Consulta;
import com.fiap.mindcare_diary.models.RecomendacaoHorario;
import com.fiap.mindcare_diary.repositories.AgendamentoRepository;
import com.fiap.mindcare_diary.services.AgendamentoService;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DataLoader {

    @Autowired
    private VectorStore vectorStore;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private AgendamentoService agendamentoService;

    public void loadConsultasIntoVectorStore(String nomeUsuarioProfissional, LocalDate dataConsulta) {
        System.out.println("📥 Deleteando dados da tabela vector_store do banco de dados PostgreSQL...");
        jdbcTemplate.execute("DELETE from vector_store");
        System.out.println("📥 Carregando dados a partir da tabela consulta do banco de dados PostgreSQL...");
        List<Document> consultas = new ArrayList<>();
        vectorStore.add(consultas);
        System.out.println("✅ Dados de estoque carregados em vector store.");
    }

}
