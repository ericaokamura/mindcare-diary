package com.fiap.mindcare_diary.models;

import com.fiap.mindcare_diary.models.enums.EstadoPaciente;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Paciente extends Usuario {

    @ManyToOne
    @JoinColumn(name = "profissional_id")
    private Profissional profissional;

    private boolean ativo;

    @Enumerated(EnumType.STRING)
    private EstadoPaciente estadoPaciente;

}
