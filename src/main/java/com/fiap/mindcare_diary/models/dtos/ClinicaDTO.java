package com.fiap.mindcare_diary.models.dtos;

import com.fiap.mindcare_diary.models.Profissional;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ClinicaDTO {

    private String nome;

    private String endereco;

    private List<ProfissionalDTO> profissionais = new ArrayList<>();

    private List<PacienteDTO> pacientes = new ArrayList<>();

    private List<ConsultaDTO> consultas = new ArrayList<>();

}
