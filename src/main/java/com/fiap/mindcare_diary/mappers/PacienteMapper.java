package com.fiap.mindcare_diary.mappers;

import com.fiap.mindcare_diary.models.Paciente;
import com.fiap.mindcare_diary.models.Usuario;
import com.fiap.mindcare_diary.models.dtos.PacienteDTO;
import com.fiap.mindcare_diary.models.dtos.ProfissionalDTO;
import com.fiap.mindcare_diary.models.enums.EstadoPaciente;
import com.fiap.mindcare_diary.models.enums.UserRole;
import org.hibernate.boot.jaxb.internal.stax.LocalSchemaLocator;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PacienteMapper {

    public static PacienteDTO convertModelToDTO(Paciente paciente) {
        if(paciente == null) return null;
        PacienteDTO dto = new PacienteDTO();
        dto.setAtivo(paciente.isAtivo());
        dto.setNomeUsuario(paciente.getNomeUsuario());
        dto.setSenha(paciente.getSenha());
        dto.setNomeCompleto(paciente.getNomeCompleto());
        dto.setDataNascimento(paciente.getDataNascimento() == null ? "" : String.valueOf(paciente.getDataNascimento()));
        dto.setDataHoraAtivacao(paciente.getDataHoraAtivacao() == null ? "" : String.valueOf(paciente.getDataHoraAtivacao()));
        //dto.getProfissionais().addAll(ProfissionalMapper.convertModelListToDTOList(paciente.getProfissionais()));
        dto.setEstadoPaciente(paciente.getEstadoPaciente() == null ? "SEM_DEFINICAO" : paciente.getEstadoPaciente().name());
        dto.setToken(paciente.getToken() == null ? "" : paciente.getToken());
        //dto.getConsultas().addAll(ConsultaMapper.convertModelListToDTOList(paciente.getConsultas()));
        //dto.getPrescricoes().addAll(PrescriptionMapper.convertModelListToDTOList(paciente.getPrescricoes()));
        dto.setUserRole(paciente.getUserRole() == null ? null : paciente.getUserRole().name());
        return dto;
    }

    public static Paciente convertDTOToModel(PacienteDTO dto) {
        if(dto == null) return null;
        Paciente model = new Paciente();
        model.setNomeUsuario(dto.getNomeUsuario());
        model.setSenha(dto.getSenha());
        model.setNomeCompleto(dto.getNomeCompleto());
        model.setDataNascimento(dto.getDataNascimento() == null ? null : LocalDate.parse(dto.getDataNascimento()));
        //model.getProfissionais().addAll(ProfissionalMapper.convertDTOListToModelList(dto.getProfissionais()));
        model.setAtivo(dto.isAtivo());
        model.setDataHoraAtivacao(dto.getDataHoraAtivacao() == null ? null : LocalDateTime.parse(dto.getDataHoraAtivacao()));
        model.setToken(dto.getToken() == null ? "" : dto.getToken());
        //model.getConsultas().addAll(ConsultaMapper.convertDTOListToModelList(dto.getConsultas()));
        //model.getPrescricoes().addAll(PrescriptionMapper.convertDTOListToModelList(dto.getPrescricoes()));
        model.setUserRole(dto.getUserRole() == null ? null : UserRole.valueOf(dto.getUserRole()));
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

    public static Paciente convertUsuarioToPaciente(Usuario usuario) {
        Paciente model = new Paciente();
        model.setNomeUsuario(usuario.getNomeUsuario());
        model.setNomeCompleto(usuario.getNomeCompleto());
        model.setAtivo(usuario.isAtivo());
        model.setDataNascimento(usuario.getDataNascimento());
        model.setDataHoraAtivacao(usuario.getDataHoraAtivacao());
        model.setToken(usuario.getToken());
        model.setGenero(usuario.getGenero());
        model.setUserRole(usuario.getUserRole());
        model.setBloqueado(usuario.isBloqueado());
        return model;
    }
}
