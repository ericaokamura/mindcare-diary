package com.fiap.mindcare_diary.models.dtos;

import com.fiap.mindcare_diary.models.Clinica;
import com.fiap.mindcare_diary.models.enums.AbordagemPsicologia;
import com.fiap.mindcare_diary.models.enums.ConsultaModalidade;
import com.fiap.mindcare_diary.models.enums.EspecialidadePsiquiatria;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProfissionalDTO extends UsuarioDTO {

    private List<PacienteDTO> pacientes = new ArrayList<>();

    private List<ConsultaDTO> consultas = new ArrayList<>();

    private String tipoProfissional;

    private String registroProfissional;

    private List<AbordagemPsicologia> abordagens = new ArrayList<>();

    private List<EspecialidadePsiquiatria> especialidades = new ArrayList<>();

    private List<ConsultaModalidade> modalidades = new ArrayList<>();

    private List<ClinicaDTO> clinicas = new ArrayList<>();

}
