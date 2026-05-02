package com.fiap.mindcare_diary.models.dtos;

import lombok.Data;

@Data
public class RegistroDiarioDTO {

    private PacienteDTO paciente;

    private String nivelHumor;

    private String pontosPositivos;

    private String dificuldadesDesafios;

    private String dataHoraCriacao;
}
