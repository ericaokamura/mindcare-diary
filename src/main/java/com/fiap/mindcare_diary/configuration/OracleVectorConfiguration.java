package com.fiap.mindcare_diary.configuration;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.oracle.OracleVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.springframework.ai.vectorstore.oracle.OracleVectorStore.OracleVectorStoreDistanceType.COSINE;
import static org.springframework.ai.vectorstore.oracle.OracleVectorStore.OracleVectorStoreIndexType.HNSW;

@Configuration
@Profile("!test")
public class OracleVectorConfiguration {

    @Bean
    public VectorStore vectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel) {
        return OracleVectorStore.builder(jdbcTemplate, embeddingModel)
                .dimensions(1536)
                .distanceType(COSINE)
                .indexType(HNSW)
                .initializeSchema(true)
                .build();
    }
}
