package com.fiap.mindcare_diary.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class RelatorioSemanal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    private String faixaDeDatas;

    @OneToMany
    private List<RegistroDiario> registrosDiarios;

    private String observacoes;

    private String recomendacoes;

    private String relatorioIA;

    private LocalDateTime dataHoraCriacao;

    private int totalPositivos;

    private int totalNegativos;
}
