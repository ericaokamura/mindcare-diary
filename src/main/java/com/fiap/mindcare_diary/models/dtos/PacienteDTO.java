package com.fiap.mindcare_diary.models.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PacienteDTO extends UsuarioDTO {

    private ProfissionalDTO profissional;

    private boolean ativo;

    private String estadoPaciente;
}
