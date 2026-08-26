package com.fiap.mindcare_diary.models;

import com.fiap.mindcare_diary.models.dtos.ProfissionalDTO;
import com.fiap.mindcare_diary.models.enums.PlanoAssinatura;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Clinica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String cnpj;

    private String endereco;

    @OneToMany(mappedBy = "clinica")
    private List<Profissional> profissionais = new ArrayList<>();

    @OneToMany(mappedBy = "clinica")
    private List<Paciente> pacientes = new ArrayList<>();

    @OneToMany(mappedBy = "clinica")
    private List<Consulta> consultas = new ArrayList<>();

    private Double taxaComissao;

    @Enumerated(EnumType.STRING)
    private PlanoAssinatura planoAssinatura;

}
