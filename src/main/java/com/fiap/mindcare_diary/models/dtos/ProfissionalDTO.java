package com.fiap.mindcare_diary.models.dtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProfissionalDTO extends UsuarioDTO {

    private List<PacienteDTO> pacientes = new ArrayList<>();

}
