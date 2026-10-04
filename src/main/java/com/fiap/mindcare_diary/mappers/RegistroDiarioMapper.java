package com.fiap.mindcare_diary.mappers;

import com.fiap.mindcare_diary.models.RegistroDiario;
import com.fiap.mindcare_diary.models.dtos.RegistroDiarioDTO;
import com.fiap.mindcare_diary.models.enums.NivelHumor;
import com.fiap.mindcare_diary.models.enums.OrigemRegistro;
import com.fiap.mindcare_diary.models.dtos.PacienteDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RegistroDiarioMapper {

    public static RegistroDiarioDTO convertModelToDTO(RegistroDiario registroDiario) {
        RegistroDiarioDTO dto = new RegistroDiarioDTO();
        dto.setId(registroDiario.getId());
        dto.setTextoConfirmado(registroDiario.getTextoConfirmado());
        dto.setOrigem(registroDiario.getOrigem() == null ? OrigemRegistro.TRADITIONAL.name() : registroDiario.getOrigem().name());
        if (registroDiario.getPaciente() != null) {
            PacienteDTO patient = new PacienteDTO();
            patient.setNomeUsuario(registroDiario.getPaciente().getNomeUsuario());
            patient.setNomeCompleto(registroDiario.getPaciente().getNomeCompleto());
            dto.setPaciente(patient);
        }
        dto.setDificuldadesDesafios(registroDiario.getDificuldadesDesafios());
        dto.setPontosPositivos(registroDiario.getPontosPositivos());
        dto.setNivelHumor(registroDiario.getNivelHumor() == null ? NivelHumor.SEM_DEFINICAO.name() : registroDiario.getNivelHumor().name());
        dto.setDataHoraCriacao(registroDiario.getDataHoraCriacao().toString());
        return dto;
    }

    public static RegistroDiario convertDTOToModel(RegistroDiarioDTO registroDiarioDTO) {
        RegistroDiario model = new RegistroDiario();
        model.setDificuldadesDesafios(registroDiarioDTO.getDificuldadesDesafios());
        model.setPontosPositivos(registroDiarioDTO.getPontosPositivos());
        model.setNivelHumor(registroDiarioDTO.getNivelHumor() == null || registroDiarioDTO.getNivelHumor().isBlank() ? NivelHumor.SEM_DEFINICAO : NivelHumor.valueOf(registroDiarioDTO.getNivelHumor()));
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
