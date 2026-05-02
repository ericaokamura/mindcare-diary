package com.fiap.mindcare_diary.mappers;

import com.fiap.mindcare_diary.models.Paciente;
import com.fiap.mindcare_diary.models.dtos.PacienteDTO;
import com.fiap.mindcare_diary.models.dtos.ProfissionalDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PacienteMapper {

    public static PacienteDTO convertModelToDTO(Paciente paciente) {
        PacienteDTO dto = new PacienteDTO();
        dto.setAtivo(paciente.isAtivo());
        dto.setNomeUsuario(paciente.getNomeUsuario());
        dto.setSenha(paciente.getSenha());
        dto.setNomeCompleto(paciente.getNomeCompleto());
        dto.setDataNascimento(paciente.getDataNascimento().toString());
        dto.setDataHoraAtivacao(paciente.getDataHoraAtivacao().toString());
        dto.setProfissional(paciente.getProfissional() == null ? null : ProfissionalMapper.convertModelToDTO(paciente.getProfissional()));
        dto.setEstadoPaciente(paciente.getEstadoPaciente() == null ? null : paciente.getEstadoPaciente().name());
        return dto;
    }

    public static Paciente convertDTOToModel(PacienteDTO dto) {
        Paciente model = new Paciente();
        model.setNomeUsuario(dto.getNomeUsuario());
        model.setSenha(dto.getSenha());
        model.setNomeCompleto(dto.getNomeCompleto());
        model.setDataNascimento(LocalDate.parse(dto.getDataNascimento()));
        return model;
    }

    public static List<PacienteDTO> convertModelListToDTOList(List<Paciente> pacientes) {
        List<PacienteDTO> dtos = new ArrayList<>();
        pacientes.forEach(p -> {
            dtos.add(convertModelToDTO(p));
        });
        return dtos;
    }
}
