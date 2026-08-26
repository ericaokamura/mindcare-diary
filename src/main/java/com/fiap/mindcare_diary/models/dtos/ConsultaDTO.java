package com.fiap.mindcare_diary.models.dtos;

import com.fiap.mindcare_diary.models.Clinica;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ConsultaDTO {

    private String number;

    private ProfissionalDTO profissional;

    private PacienteDTO paciente;

    private ClinicaDTO clinica;

    private Double valorConsulta;

    private String dataHoraConsulta;

    private boolean atendida;

    private boolean cancelada;

}
