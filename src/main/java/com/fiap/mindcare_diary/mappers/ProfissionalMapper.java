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
        List<PacienteDTO> pacientes = new ArrayList<>();
        ProfissionalDTO profissionalDTO = new ProfissionalDTO();
        profissionalDTO.setNomeUsuario(profissional.getNomeUsuario());
        profissionalDTO.setSenha(profissional.getSenha());
        profissionalDTO.setNomeCompleto(profissional.getNomeCompleto());
        profissionalDTO.setDataHoraAtivacao(profissional.getDataHoraAtivacao().toString());
        profissionalDTO.setDataNascimento(profissional.getDataNascimento().toString());
        profissionalDTO.setPacientes(pacientes);
        profissional.getPacientes().forEach(p -> {
            PacienteDTO dto = new PacienteDTO();
            dto.setProfissional(profissionalDTO);
            dto.setAtivo(p.isAtivo());
            dto.setEstadoPaciente(p.getEstadoPaciente().name());
            pacientes.add(dto);
        });
        profissionalDTO.setTipoProfissional(profissional.getTipoProfissional().name());
        return profissionalDTO;
    }

    public static Profissional convertDTOToModel(ProfissionalDTO dto) {
        Profissional profissional = new Profissional();
        profissional.setNomeUsuario(dto.getNomeUsuario());
        profissional.setSenha(dto.getSenha());
        profissional.setNomeCompleto(dto.getNomeCompleto());
        profissional.setDataNascimento(LocalDate.parse(dto.getDataNascimento()));
        profissional.setTipoProfissional(TipoProfissional.valueOf(dto.getTipoProfissional()));
        return profissional;
    }


    public static List<ProfissionalDTO> convertModelListToDTOList(List<Profissional> profissionais) {
        List<ProfissionalDTO> dtos = new ArrayList<>();
        profissionais.forEach(p -> {
            dtos.add(convertModelToDTO(p));
        });
        return dtos;
    }
}
