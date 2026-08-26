package com.fiap.mindcare_diary.mappers;

import com.fiap.mindcare_diary.models.RegistroDiario;
import com.fiap.mindcare_diary.models.dtos.RegistroDiarioDTO;
import com.fiap.mindcare_diary.models.enums.NivelHumor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RegistroDiarioMapper {

    public static RegistroDiarioDTO convertModelToDTO(RegistroDiario registroDiario) {
        RegistroDiarioDTO dto = new RegistroDiarioDTO();
        dto.setPaciente(PacienteMapper.convertModelToDTO(registroDiario.getPaciente()));
        dto.setDificuldadesDesafios(registroDiario.getDificuldadesDesafios());
        dto.setPontosPositivos(registroDiario.getPontosPositivos());
        dto.setNivelHumor(registroDiario.getNivelHumor() == null ? "SEM_DEFINICAO" : registroDiario.getNivelHumor().name());
        dto.setDataHoraCriacao(registroDiario.getDataHoraCriacao().toString());
        return dto;
    }

    public static RegistroDiario convertDTOToModel(RegistroDiarioDTO registroDiarioDTO) {
        RegistroDiario model = new RegistroDiario();
        model.setPaciente(PacienteMapper.convertDTOToModel(registroDiarioDTO.getPaciente()));
        model.setDataHoraCriacao(LocalDateTime.parse(registroDiarioDTO.getDataHoraCriacao()));
        model.setDificuldadesDesafios(registroDiarioDTO.getDificuldadesDesafios());
        model.setPontosPositivos(registroDiarioDTO.getPontosPositivos());
        model.setNivelHumor(registroDiarioDTO.getNivelHumor().isBlank() ? NivelHumor.SEM_DEFINICAO : NivelHumor.valueOf(registroDiarioDTO.getNivelHumor()));
        return model;
    }

    public static List<RegistroDiarioDTO> convertModelListToDTOList(List<RegistroDiario> registroDiarios) {
        List<RegistroDiarioDTO> dtos = new ArrayList<>();
        for (RegistroDiario registroDiario : registroDiarios) {
            dtos.add(convertModelToDTO(registroDiario));
        }
        return dtos;
    }
}
