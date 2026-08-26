package com.fiap.mindcare_diary.mappers;

import com.fiap.mindcare_diary.models.Clinica;
import com.fiap.mindcare_diary.models.dtos.ClinicaDTO;
import com.fiap.mindcare_diary.models.enums.PlanoAssinatura;

import java.util.List;

public class ClinicaMapper {

    public static ClinicaDTO convertModelToDTO(Clinica model) {
        if(model == null) return null;
        ClinicaDTO dto = new ClinicaDTO();
        dto.setNome(model.getNome());
        dto.setEndereco(model.getEndereco());
        //dto.getPacientes().addAll(PacienteMapper.convertModelListToDTOList(model.getPacientes()));
        //dto.getProfissionais().addAll(ProfissionalMapper.convertModelListToDTOList(model.getProfissionais()));
        //dto.getConsultas().addAll(ConsultaMapper.convertModelListToDTOList(model.getConsultas()));
        dto.setTaxaComissao(model.getTaxaComissao());
        dto.setPlanoAssinatura(model.getPlanoAssinatura().name());
        dto.setCnpj(model.getCnpj());
        return dto;
    }

    public static Clinica convertDTOToModel(ClinicaDTO dto) {
        if(dto == null) return null;
        Clinica model = new Clinica();
        model.setNome(dto.getNome());
        model.setCnpj(dto.getCnpj());
        model.setEndereco(dto.getEndereco());
        //model.getPacientes().addAll(PacienteMapper.convertDTOListToModelList(dto.getPacientes()));
        //model.getProfissionais().addAll(ProfissionalMapper.convertDTOListToModelList(dto.getProfissionais()));
        //model.getConsultas().addAll(ConsultaMapper.convertDTOListToModelList(dto.getConsultas()));
        model.setTaxaComissao(dto.getTaxaComissao());
        model.setPlanoAssinatura(PlanoAssinatura.valueOf(dto.getPlanoAssinatura()));
        return model;
    }

    public static List<ClinicaDTO> convertModelListToDTOList(List<Clinica> clinicas) {
        return clinicas.stream().map(ClinicaMapper::convertModelToDTO).toList();
    }

    public static List<Clinica> convertDTOListToModelList(List<ClinicaDTO> dtos) {
        return dtos.stream().map(ClinicaMapper::convertDTOToModel).toList();
    }
}
