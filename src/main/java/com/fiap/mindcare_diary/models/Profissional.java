package com.fiap.mindcare_diary.models;

import com.fiap.mindcare_diary.models.enums.AbordagemPsicologia;
import com.fiap.mindcare_diary.models.enums.ConsultaModalidade;
import com.fiap.mindcare_diary.models.enums.EspecialidadePsiquiatria;
import com.fiap.mindcare_diary.models.enums.TipoProfissional;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Profissional extends Usuario {

    @ManyToMany(mappedBy = "profissionais")
    private List<Paciente> pacientes = new ArrayList<>();

    @OneToMany(mappedBy = "profissional")
    private List<Consulta> consultas = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private TipoProfissional tipoProfissional = TipoProfissional.PSICOLOGO;

    private String registroProfissional;

    @ElementCollection
    @CollectionTable(
            name = "prescription_abordagens",
            joinColumns = @JoinColumn(name = "profissional_id")
    )
    @Column(name = "abordagens")
    private List<AbordagemPsicologia> abordagens = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "prescription_especialidades",
            joinColumns = @JoinColumn(name = "profissional_id")
    )
    @Column(name = "especialidades")
    private List<EspecialidadePsiquiatria> especialidades = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "prescription_modalidades",
            joinColumns = @JoinColumn(name = "profissional_id")
    )
    @Column(name = "modalidades")
    private List<ConsultaModalidade> modalidades = new ArrayList<>();

    @ManyToOne
    private Clinica clinica;

}
