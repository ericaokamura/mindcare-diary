package com.fiap.mindcare_diary.mappers;

import com.fiap.mindcare_diary.models.Paciente;
import com.fiap.mindcare_diary.models.Profissional;
import com.fiap.mindcare_diary.models.Usuario;
import com.fiap.mindcare_diary.models.dtos.PacienteDTO;
import com.fiap.mindcare_diary.models.dtos.ProfissionalDTO;
import com.fiap.mindcare_diary.models.enums.EstadoPaciente;
import com.fiap.mindcare_diary.models.enums.Sexo;
import com.fiap.mindcare_diary.models.enums.TipoProfissional;
import com.fiap.mindcare_diary.models.enums.UserRole;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProfissionalMapper {

    public static ProfissionalDTO convertModelToDTO(Profissional profissional) {
        if(profissional == null) return null;
        ProfissionalDTO profissionalDTO = new ProfissionalDTO();
        profissionalDTO.setNomeUsuario(profissional.getNomeUsuario());
        profissionalDTO.setSenha(profissional.getSenha());
        profissionalDTO.setNomeCompleto(profissional.getNomeCompleto());
        profissionalDTO.setDataHoraAtivacao(String.valueOf(profissional.getDataHoraAtivacao()));
        profissionalDTO.setDataNascimento(profissional.getDataNascimento() == null ? "" : String.valueOf(profissional.getDataNascimento()));
        profissionalDTO.setGenero(profissional.getGenero() == null ? null : profissional.getGenero().name());
        profissionalDTO.setAtivo(profissional.isAtivo());
        profissionalDTO.setToken(profissional.getToken());
        //profissionalDTO.getPacientes().addAll(PacienteMapper.convertModelListToDTOList(profissional.getPacientes()));
        profissionalDTO.setTipoProfissional(profissional.getTipoProfissional().name());
        //profissionalDTO.getConsultas().addAll(ConsultaMapper.convertModelListToDTOList(profissional.getConsultas()));
        profissionalDTO.setRegistroProfissional(profissional.getRegistroProfissional());
        profissionalDTO.getAbordagens().addAll(profissional.getAbordagens());
        profissionalDTO.getEspecialidades().addAll(profissional.getEspecialidades());
        profissionalDTO.setUserRole(profissional.getUserRole() == null ? null : profissional.getUserRole().name());
        //profissionalDTO.getClinicas().addAll(ClinicaMapper.convertModelListToDTOList(profissional.getClinicas()));
        return profissionalDTO;
    }

    public static Profissional convertDTOToModel(ProfissionalDTO dto) {
        if(dto == null) return null;
        Profissional profissional = new Profissional();
        profissional.setNomeUsuario(dto.getNomeUsuario());
        profissional.setSenha(dto.getSenha());
        profissional.setNomeCompleto(dto.getNomeCompleto());
        profissional.setDataNascimento(dto.getDataNascimento() == null ? null : LocalDate.parse(dto.getDataNascimento()));
        //profissional.getPacientes().addAll(PacienteMapper.convertDTOListToModelList(dto.getPacientes()));
        profissional.setGenero(dto.getGenero() == null ? null : Sexo.valueOf(dto.getGenero()));
        profissional.setToken(dto.getToken());
        //profissional.getConsultas().addAll(ConsultaMapper.convertDTOListToModelList(dto.getConsultas()));
        profissional.setAtivo(dto.isAtivo());
        profissional.setDataHoraAtivacao(dto.getDataHoraAtivacao() == null ? null : LocalDateTime.parse(dto.getDataHoraAtivacao()));
        profissional.setRegistroProfissional(dto.getRegistroProfissional());
        profissional.getAbordagens().addAll(dto.getAbordagens());
        profissional.getEspecialidades().addAll(dto.getEspecialidades());
        profissional.setUserRole(dto.getUserRole() == null ? null : UserRole.valueOf(dto.getUserRole()));
        //profissional.getClinicas().addAll(ClinicaMapper.convertDTOListToModelList(dto.getClinicas()));
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

    public static Profissional convertUsuarioToProfissional(Usuario usuarioSalvo) {
        Profissional profissional = new Profissional();
        profissional.setNomeUsuario(usuarioSalvo.getNomeUsuario());
        profissional.setNomeCompleto(usuarioSalvo.getNomeCompleto());
        profissional.setDataNascimento(usuarioSalvo.getDataNascimento());
        profissional.setDataHoraAtivacao(usuarioSalvo.getDataHoraAtivacao());
        profissional.setUserRole(usuarioSalvo.getUserRole());
        profissional.setAtivo(usuarioSalvo.isAtivo());
        profissional.setBloqueado(usuarioSalvo.isBloqueado());
        profissional.setGenero(usuarioSalvo.getGenero());
        profissional.setToken(usuarioSalvo.getToken());
        return profissional;
    }
}
