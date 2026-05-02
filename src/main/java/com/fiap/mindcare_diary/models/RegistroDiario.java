package com.fiap.mindcare_diary.models;

import com.fiap.mindcare_diary.models.enums.NivelHumor;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class RegistroDiario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Paciente paciente;

    private NivelHumor nivelHumor;

    private String pontosPositivos;

    private String dificuldadesDesafios;

    private LocalDateTime dataHoraCriacao;
}
