package com.fiap.mindcare_diary.models;

import com.fiap.mindcare_diary.models.enums.NivelHumor;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class RegistroDiario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @Enumerated(EnumType.STRING)
    private NivelHumor nivelHumor;

    private String pontosPositivos;

    private String dificuldadesDesafios;

    private LocalDateTime dataHoraCriacao;
}
