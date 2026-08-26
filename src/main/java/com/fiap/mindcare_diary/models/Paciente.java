package com.fiap.mindcare_diary.models;

import com.fiap.mindcare_diary.models.enums.EstadoPaciente;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("PACIENTE")
@Getter
@Setter
public class Paciente extends Usuario {

    @ManyToMany
    @JoinTable(
            name = "paciente_profissional",
            joinColumns = @JoinColumn(name = "paciente_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "profissional_id", referencedColumnName = "id")
    )
    private List<Profissional> profissionais = new ArrayList<>();

    @ManyToMany
    private List<Clinica> clinicas = new ArrayList<>();

    @OneToMany(mappedBy = "paciente")
    private List<Consulta> consultas = new ArrayList<>();

    @OneToMany(mappedBy = "paciente")
    private List<Prescription> prescricoes = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private EstadoPaciente estadoPaciente = EstadoPaciente.ESTAVEL;

}
