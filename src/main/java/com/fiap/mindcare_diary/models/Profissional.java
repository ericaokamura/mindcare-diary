package com.fiap.mindcare_diary.models;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Profissional extends Usuario {

    @OneToMany
    private List<Paciente> pacientes;

}
