package com.fiap.mindcare_diary.mappers;

import com.fiap.mindcare_diary.models.RelatorioSemanal;
import com.fiap.mindcare_diary.models.dtos.RelatorioSemanalDTO;

import java.util.ArrayList;
import java.util.List;

public class RelatorioSemanalMapper {

    public static List<RelatorioSemanalDTO> convertModelListToDTOList(List<RelatorioSemanal> relatorios) {
        List<RelatorioSemanalDTO> dtos = new ArrayList<>();
        for (RelatorioSemanal relatorio : relatorios) {
            RelatorioSemanalDTO dto = new RelatorioSemanalDTO();
            dto.setFaixaDeDatas(relatorio.getFaixaDeDatas());
            dto.setPaciente(PacienteMapper.convertModelToDTO(relatorio.getPaciente()));
            dto.setRegistrosDiarios(RegistroDiarioMapper.convertModelListToDTOList(relatorio.getRegistrosDiarios()));
            dto.setObservacoes(relatorio.getObservacoes());
            dto.setRecomendacoes(relatorio.getRecomendacoes());
            dto.setRelatorioIA(relatorio.getRelatorioIA());
            dto.setDataHoraCriacao(relatorio.getDataHoraCriacao().toString());
            dto.setTotalNegativos(relatorio.getTotalNegativos());
            dto.setTotalPositivos(relatorio.getTotalPositivos());
            dto.setResumo(relatorio.getResumo());
            dto.setNumber(relatorio.getNumber());
            dtos.add(dto);
        }
        return dtos;
    }

    public static RelatorioSemanalDTO convertModelToDTO(RelatorioSemanal relatorio) {
        RelatorioSemanalDTO dto = new RelatorioSemanalDTO();
        dto.setFaixaDeDatas(relatorio.getFaixaDeDatas());
        dto.setPaciente(PacienteMapper.convertModelToDTO(relatorio.getPaciente()));
        dto.setRegistrosDiarios(RegistroDiarioMapper.convertModelListToDTOList(relatorio.getRegistrosDiarios()));
        dto.setObservacoes(relatorio.getObservacoes());
        dto.setRecomendacoes(relatorio.getRecomendacoes());
        dto.setRelatorioIA(relatorio.getRelatorioIA());
        dto.setDataHoraCriacao(relatorio.getDataHoraCriacao().toString());
        dto.setTotalNegativos(relatorio.getTotalNegativos());
        dto.setTotalPositivos(relatorio.getTotalPositivos());
        dto.setResumo(relatorio.getResumo());
        dto.setNumber(relatorio.getNumber());
        return dto;
    }
}
