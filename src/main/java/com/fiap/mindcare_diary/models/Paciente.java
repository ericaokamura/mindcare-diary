package com.fiap.mindcare_diary.models;

import com.fiap.mindcare_diary.models.enums.EstadoPaciente;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class Paciente extends Usuario {

    @OneToOne
    private Profissional profissional;

    private boolean ativo;

    private EstadoPaciente estadoPaciente;

}
