package com.fiap.mindcare_diary.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class RelatorioSemanal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Paciente paciente;

    private String faixaDeDatas;

    @OneToMany
    private List<RegistroDiario> registrosDiarios;

    private String observacoes;

    private String recomendacoes;

    private String relatorioIA;

    private LocalDateTime dataHoraCriacao;
}
