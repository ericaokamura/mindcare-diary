package com.fiap.mindcare_diary.models;

import com.fiap.mindcare_diary.models.enums.EstadoPaciente;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Paciente extends Usuario {

    @ManyToOne
    @JoinColumn(name = "profissional_id")
    private Profissional profissional;

    @OneToMany(mappedBy = "paciente")
    private List<Consulta> consultas = new ArrayList<>();

    @OneToMany(mappedBy = "paciente")
    private List<Prescription> prescricoes = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private EstadoPaciente estadoPaciente = EstadoPaciente.SEM_DEFINICAO;

}
