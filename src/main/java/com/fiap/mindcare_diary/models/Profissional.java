package com.fiap.mindcare_diary.models;

import com.fiap.mindcare_diary.models.enums.TipoProfissional;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Profissional extends Usuario {

    @OneToMany(mappedBy = "profissional")
    private List<Paciente> pacientes;

    @OneToMany(mappedBy = "profissional")
    private List<Consulta> consultas;

    @Enumerated(EnumType.STRING)
    private TipoProfissional tipoProfissional;

}
