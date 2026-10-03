//package com.fiap.mindcare_diary.configuration;
//
//import org.springframework.ai.embedding.EmbeddingModel;
//import org.springframework.ai.vectorstore.VectorStore;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jdbc.core.JdbcTemplate;
//
//import static org.springframework.ai.vectorstore.dump.OracleVectorStore.OracleVectorStoreDistanceType.COSINE;
//import static org.springframework.ai.vectorstore.dump.OracleVectorStore.OracleVectorStoreIndexType.HNSW;
//
//@Configuration
//public class OracleVectorConfiguration {
//
//    @Bean
//    public VectorStore vectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel) {
//        return OracleVector.builder(jdbcTemplate, embeddingModel)
//                .dimensions(1536)
//                .distanceType(COSINE)
//                .indexType(HNSW)
//                .initializeSchema(true)
//                .schemaName("public")
//                .vectorTableName("vector_store")
//                .maxDocumentBatchSize(10000)
//                .build();
//    }
//}
//
