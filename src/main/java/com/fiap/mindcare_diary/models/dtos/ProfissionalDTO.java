package com.fiap.mindcare_diary.models.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProfissionalDTO extends UsuarioDTO {

    private List<PacienteDTO> pacientes = new ArrayList<>();

    private String tipoProfissional;

}
