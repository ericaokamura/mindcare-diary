package com.fiap.mindcare_diary.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String number;

    private LocalDate issueDate;

    private LocalDate  expirationDate;

    private Long daysRemaining;

    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "profissional_id")
    private Profissional doctorInfo;

    @ElementCollection
    private List<String> medicines;

    private boolean controlled;

    private boolean valid;

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
