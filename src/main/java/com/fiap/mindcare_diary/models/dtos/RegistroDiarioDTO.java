package com.fiap.mindcare_diary.models.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistroDiarioDTO {

    private Long id;

    private String textoConfirmado;

    private String origem;

    private PacienteDTO paciente;

    private String nivelHumor;

    private String pontosPositivos;

    private String dificuldadesDesafios;

    private String dataHoraCriacao;
}
