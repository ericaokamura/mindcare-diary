package com.fiap.mindcare_diary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MindcareDiaryApplication {

	public static void main(String[] args) throws Exception {
		if (java.util.Arrays.asList(args).contains("--migrate-record-encryption")) {
			com.fiap.mindcare_diary.security.storage.EncryptionMigration.main(args);
			return;
		}
		SpringApplication.run(MindcareDiaryApplication.class, args);
	}

}
