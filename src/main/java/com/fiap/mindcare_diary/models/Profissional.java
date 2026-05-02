package com.fiap.mindcare_diary.models;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Profissional extends Usuario {

    @OneToMany
    private List<Paciente> pacientes;

}
