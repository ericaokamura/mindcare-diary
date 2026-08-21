package com.fiap.mindcare_diary.models;

import com.fiap.mindcare_diary.models.enums.AbordagemPsicologia;
import com.fiap.mindcare_diary.models.enums.ConsultaModalidade;
import com.fiap.mindcare_diary.models.enums.EspecialidadePsiquiatria;
import com.fiap.mindcare_diary.models.enums.TipoProfissional;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Profissional extends Usuario {

    @ManyToMany
    private List<Paciente> pacientes = new ArrayList<>();

    @OneToMany(mappedBy = "profissional")
    private List<Consulta> consultas = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private TipoProfissional tipoProfissional;

    private List<AbordagemPsicologia> abordagens = new ArrayList<>();

    private List<EspecialidadePsiquiatria> especialidades = new ArrayList<>();

    private List<ConsultaModalidade> modalidades = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Clinica> clinicas = new ArrayList<>();

}
