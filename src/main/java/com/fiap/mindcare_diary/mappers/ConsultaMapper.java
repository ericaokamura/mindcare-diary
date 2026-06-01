package com.fiap.mindcare_diary.mappers;

import com.fiap.mindcare_diary.models.Consulta;
import com.fiap.mindcare_diary.models.Profissional;
import com.fiap.mindcare_diary.models.dtos.ConsultaDTO;
import com.fiap.mindcare_diary.models.dtos.ProfissionalDTO;
import com.fiap.mindcare_diary.models.enums.TipoProfissional;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ConsultaMapper {

    public static Consulta convertDTOToModel(ConsultaDTO dto) {
        Consulta consulta = new Consulta();
        consulta.setProfissional(ProfissionalMapper.convertDTOToModel(dto.getProfissional()));
        consulta.setDataHoraConsulta(LocalDateTime.parse(dto.getDataHoraConsulta()));
        consulta.setAtendida(dto.isAtendida());
        consulta.setCancelada(dto.isCancelada());
        return consulta;
    }
}
