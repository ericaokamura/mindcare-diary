package com.fiap.mindcare_diary.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Entity @Getter @Setter
public class AceiteTermos {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false) private Usuario usuario;
    private String versao;
    private String hashDocumentos;
    private Instant aceitoEm;
}
