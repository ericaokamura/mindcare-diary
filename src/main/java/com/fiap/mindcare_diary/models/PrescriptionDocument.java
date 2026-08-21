package com.fiap.mindcare_diary.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class PrescriptionDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "prescription_id")
    private Prescription prescription;

    @Column(name = "arquivo_pdf", columnDefinition = "bytea")
    private byte[] arquivoPdf;

    @Column(name = "nome_arquivo")
    private String nomeArquivo;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "tamanho_bytes")
    private Long tamanhoBytes;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm;

}
