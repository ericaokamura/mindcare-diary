package com.fiap.mindcare_diary.mappers;

import com.fiap.mindcare_diary.models.Consulta;
import com.fiap.mindcare_diary.models.Profissional;
import com.fiap.mindcare_diary.models.dtos.ConsultaDTO;
import com.fiap.mindcare_diary.models.dtos.ProfissionalDTO;
import com.fiap.mindcare_diary.models.enums.TipoProfissional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConsultaMapper {

    public static Consulta convertDTOToModel(ConsultaDTO dto) {
        Consulta consulta = new Consulta();
        consulta.setProfissional(ProfissionalMapper.convertDTOToModel(dto.getProfissional()));
        consulta.setDataHoraConsulta(LocalDateTime.parse(dto.getDataHoraConsulta()));
        consulta.setAtendida(dto.isAtendida());
        consulta.setCancelada(dto.isCancelada());
        consulta.setPaciente(PacienteMapper.convertDTOToModel(dto.getPaciente()));
        return consulta;
    }

    public static ConsultaDTO convertModelToDTO(Consulta model) {
        ConsultaDTO dto = new ConsultaDTO();
        dto.setProfissional(ProfissionalMapper.convertModelToDTO(model.getProfissional()));
        dto.setDataHoraConsulta(model.getDataHoraConsulta().toString());
        dto.setAtendida(model.isAtendida());
        dto.setCancelada(model.isCancelada());
        dto.setPaciente(PacienteMapper.convertModelToDTO(model.getPaciente()));
        return dto;
    }

    public static List<Consulta> convertDTOListToModelList(List<ConsultaDTO> dtos) {
        List<Consulta> consultas = new ArrayList<>();
        dtos.forEach(dto -> {
            consultas.add(convertDTOToModel(dto));
        });
        return consultas;
    }

    public static List<ConsultaDTO> convertModelListToDTOList(List<Consulta> consultas) {
        List<ConsultaDTO> dtos = new ArrayList<>();
        consultas.forEach(consulta -> {
            dtos.add(convertModelToDTO(consulta));
        });
        return dtos;
    }
}
