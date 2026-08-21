package com.fiap.mindcare_diary.mappers;

import com.fiap.mindcare_diary.models.Paciente;
import com.fiap.mindcare_diary.models.Profissional;
import com.fiap.mindcare_diary.models.dtos.PacienteDTO;
import com.fiap.mindcare_diary.models.dtos.ProfissionalDTO;
import com.fiap.mindcare_diary.models.enums.EstadoPaciente;
import com.fiap.mindcare_diary.models.enums.TipoProfissional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProfissionalMapper {

    public static ProfissionalDTO convertModelToDTO(Profissional profissional) {
        ProfissionalDTO profissionalDTO = new ProfissionalDTO();
        profissionalDTO.setNomeUsuario(profissional.getNomeUsuario());
        profissionalDTO.setSenha(profissional.getSenha());
        profissionalDTO.setNomeCompleto(profissional.getNomeCompleto());
        profissionalDTO.setDataHoraAtivacao(String.valueOf(profissional.getDataHoraAtivacao()));
        profissionalDTO.setDataNascimento(String.valueOf(profissional.getDataNascimento()));
        profissionalDTO.setAtivo(profissional.isAtivo());
        profissionalDTO.setToken(profissional.getToken());
        profissionalDTO.setPacientes(PacienteMapper.convertModelListToDTOList(profissional.getPacientes()));
        profissionalDTO.setTipoProfissional(profissional.getTipoProfissional().name());
        profissionalDTO.setConsultas(ConsultaMapper.convertModelListToDTOList(profissional.getConsultas()));
        return profissionalDTO;
    }

    public static Profissional convertDTOToModel(ProfissionalDTO dto) {
        Profissional profissional = new Profissional();
        profissional.setNomeUsuario(dto.getNomeUsuario());
        profissional.setSenha(dto.getSenha());
        profissional.setNomeCompleto(dto.getNomeCompleto());
        profissional.setDataNascimento(LocalDate.parse(dto.getDataNascimento()));
        profissional.setTipoProfissional(TipoProfissional.valueOf(dto.getTipoProfissional()));
        profissional.setPacientes(PacienteMapper.convertDTOListToModelList(dto.getPacientes()));
        profissional.setToken(dto.getToken());
        profissional.setConsultas(ConsultaMapper.convertDTOListToModelList(dto.getConsultas()));
        profissional.setAtivo(dto.isAtivo());
        profissional.setDataHoraAtivacao(dto.getDataHoraAtivacao() == null ? null : LocalDateTime.parse(dto.getDataHoraAtivacao()));
        profissional.setPacientes(PacienteMapper.convertDTOListToModelList(dto.getPacientes()));
        profissional.setConsultas(ConsultaMapper.convertDTOListToModelList(dto.getConsultas()));
        return profissional;
    }


    public static List<ProfissionalDTO> convertModelListToDTOList(List<Profissional> profissionais) {
        List<ProfissionalDTO> dtos = new ArrayList<>();
        profissionais.forEach(p -> {
            dtos.add(convertModelToDTO(p));
        });
        return dtos;
    }

    public static List<Profissional> convertDTOListToModelList(List<ProfissionalDTO> dtos) {
        List<Profissional> profissionais = new ArrayList<>();
        dtos.forEach(dto -> {
            profissionais.add(convertDTOToModel(dto));
        });
        return profissionais;
    }
}
