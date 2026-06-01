package com.fiap.mindcare_diary.mappers;

import com.fiap.mindcare_diary.models.Paciente;
import com.fiap.mindcare_diary.models.dtos.PacienteDTO;
import com.fiap.mindcare_diary.models.dtos.ProfissionalDTO;
import com.fiap.mindcare_diary.models.enums.EstadoPaciente;
import org.hibernate.boot.jaxb.internal.stax.LocalSchemaLocator;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PacienteMapper {

    public static PacienteDTO convertModelToDTO(Paciente paciente) {
        PacienteDTO dto = new PacienteDTO();
        dto.setAtivo(paciente.isAtivo());
        dto.setNomeUsuario(paciente.getNomeUsuario());
        dto.setSenha(paciente.getSenha());
        dto.setNomeCompleto(paciente.getNomeCompleto());
        dto.setDataNascimento(paciente.getDataNascimento() == null ? "" : String.valueOf(paciente.getDataNascimento()));
        dto.setDataHoraAtivacao(paciente.getDataHoraAtivacao() == null ? "" : String.valueOf(paciente.getDataHoraAtivacao()));
        dto.setProfissional(paciente.getProfissional() == null ? null : ProfissionalMapper.convertModelToDTO(paciente.getProfissional()));
        dto.setEstadoPaciente(paciente.getEstadoPaciente() == null ? "SEM_DEFINICAO" : paciente.getEstadoPaciente().name());
        //dto.setConsultas(ConsultaMapper.convertModelListToDTOList(paciente.getConsultas()));
        return dto;
    }

    public static Paciente convertDTOToModel(PacienteDTO dto) {
        Paciente model = new Paciente();
        model.setNomeUsuario(dto.getNomeUsuario());
        model.setSenha(dto.getSenha());
        model.setNomeCompleto(dto.getNomeCompleto());
        model.setDataNascimento(dto.getDataNascimento() == null ? null : LocalDate.parse(dto.getDataNascimento()));
        model.setProfissional(dto.getProfissional() == null ? null : ProfissionalMapper.convertDTOToModel(dto.getProfissional()));
        model.setAtivo(dto.isAtivo());
        model.setEstadoPaciente(dto.getEstadoPaciente() == null ? EstadoPaciente.SEM_DEFINICAO : EstadoPaciente.valueOf(dto.getEstadoPaciente()));
        model.setDataHoraAtivacao(dto.getDataHoraAtivacao() == null ? null : LocalDateTime.parse(dto.getDataHoraAtivacao()));
        //model.setConsultas(ConsultaMapper.convertDTOListToModelList(dto.getConsultas()));
        return model;
    }

    public static List<PacienteDTO> convertModelListToDTOList(List<Paciente> pacientes) {
        List<PacienteDTO> dtos = new ArrayList<>();
        pacientes.forEach(p -> {
            dtos.add(convertModelToDTO(p));
        });
        return dtos;
    }

    public static List<Paciente> convertDTOListToModelList(List<PacienteDTO> dtos) {
        List<Paciente> pacientes = new ArrayList<>();
        dtos.forEach(p -> {
            pacientes.add(convertDTOToModel(p));
        });
        return pacientes;
    }
}
