package com.fiap.mindcare_diary.mappers;

import com.fiap.mindcare_diary.models.Clinica;
import com.fiap.mindcare_diary.models.dtos.ClinicaDTO;

public class ClinicaMapper {

    public static ClinicaDTO convertModelToDTO(Clinica model) {
        ClinicaDTO dto = new ClinicaDTO();
        dto.setNome(model.getNome());
        dto.setEndereco(model.getEndereco());
        dto.setPacientes(PacienteMapper.convertModelListToDTOList(model.getPacientes()));
        dto.setProfissionais(ProfissionalMapper.convertModelListToDTOList(model.getProfissionais()));
        dto.setConsultas(ConsultaMapper.convertModelListToDTOList(model.getConsultas()));
        return dto;
    }

    public static Clinica convertDTOToModel(ClinicaDTO dto) {
        Clinica model = new Clinica();
        model.setNome(dto.getNome());
        model.setEndereco(dto.getEndereco());
        model.setPacientes(PacienteMapper.convertDTOListToModelList(dto.getPacientes()));
        model.setProfissionais(ProfissionalMapper.convertDTOListToModelList(dto.getProfissionais()));
        model.setConsultas(ConsultaMapper.convertDTOListToModelList(dto.getConsultas()));
        return model;
    }

}
