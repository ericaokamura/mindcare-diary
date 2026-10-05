package com.fiap.mindcare_diary.security.storage;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EncryptedStorageConfiguration {
    /** Verification during bean creation, before accepting HTTP requests. Never migrates data automatically. */
    @Bean
    public Object encryptedStorageVerified(EntityManagerFactory entityManagerFactory, DataSource dataSource, RecordCrypto crypto) throws Exception {
        try (var connection = dataSource.getConnection()) { EncryptionMigration.verify(connection, crypto); }
        return new Object();
    }
}
