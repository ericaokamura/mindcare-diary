package com.fiap.mindcare_diary.models.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ConsultaDTO {

    private ProfissionalDTO profissional;

    private String dataHoraConsulta;

    private boolean atendida;

    private boolean cancelada;

}
