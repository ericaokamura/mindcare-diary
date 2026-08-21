package com.fiap.mindcare_diary.models.dtos;

import com.fiap.mindcare_diary.models.Prescription;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PrescriptionDocumentDTO {

    private PrescriptionDTO prescription;

    private byte[] arquivoPdf;

    private String nomeArquivo;

    private String contentType;

    private Long tamanhoBytes;

    private LocalDateTime criadoEm;
}
