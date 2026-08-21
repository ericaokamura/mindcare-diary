package com.fiap.mindcare_diary.models.dtos;

import com.fiap.mindcare_diary.models.Clinica;
import com.fiap.mindcare_diary.models.Prescription;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PacienteDTO extends UsuarioDTO {

    private List<ProfissionalDTO> profissionais = new ArrayList<>();

    private List<ConsultaDTO> consultas = new ArrayList<>();

    private List<Clinica> clinicas = new ArrayList<>();

    private List<PrescriptionDTO> prescricoes = new ArrayList<>();

    private String estadoPaciente = "SEM_DEFINICAO";
}
