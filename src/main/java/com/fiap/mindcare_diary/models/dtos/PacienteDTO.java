package com.fiap.mindcare_diary.models.dtos;

import com.fiap.mindcare_diary.models.Profissional;
import com.fiap.mindcare_diary.models.enums.EstadoPaciente;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
public class PacienteDTO extends UsuarioDTO {

    private ProfissionalDTO profissional;

    private boolean ativo;

    private String estadoPaciente;
}
