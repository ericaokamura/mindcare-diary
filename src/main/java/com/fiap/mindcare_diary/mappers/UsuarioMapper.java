package com.fiap.mindcare_diary.mappers;

import com.fiap.mindcare_diary.models.Paciente;
import com.fiap.mindcare_diary.models.Usuario;
import com.fiap.mindcare_diary.models.dtos.UsuarioDTO;
import com.fiap.mindcare_diary.models.enums.Sexo;
import com.fiap.mindcare_diary.models.enums.UserRole;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UsuarioMapper {

    public static List<UsuarioDTO> convertListModelToListDTO(List<Usuario> usuarios) {
        List<UsuarioDTO> dtos = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            UsuarioDTO dto = new UsuarioDTO();
            dto.setNomeUsuario(usuario.getNomeUsuario());
            dto.setNomeCompleto(usuario.getNomeCompleto());
            dto.setDataNascimento(usuario.getDataNascimento().toString());
            dto.setDataHoraAtivacao(usuario.getDataHoraAtivacao().toString());
            dto.setToken(usuario.getToken());
            dto.setGenero(usuario.getGenero().name());
            dto.setAtivo(usuario.isAtivo());
            dto.setUserRole(usuario.getUserRole().name());
            dtos.add(dto);
        }
        return dtos;
    }

    public static Usuario convertDTOToModel(UsuarioDTO dto) {
        Usuario model = new Usuario();
        model.setNomeUsuario(dto.getNomeUsuario());
        model.setNomeCompleto(dto.getNomeCompleto());
        model.setDataNascimento(LocalDate.parse(dto.getDataNascimento()));
        model.setToken(dto.getToken());
        model.setGenero(Sexo.valueOf(dto.getGenero()));
        model.setAtivo(dto.isAtivo());
        model.setUserRole(UserRole.valueOf(dto.getUserRole()));
        return model;
    }

    public static UsuarioDTO convertModelToDTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNomeUsuario(usuario.getNomeUsuario());
        dto.setNomeCompleto(usuario.getNomeCompleto());
        dto.setDataNascimento(usuario.getDataNascimento().toString());
        dto.setDataHoraAtivacao(usuario.getDataHoraAtivacao().toString());
        dto.setToken(usuario.getToken());
        dto.setGenero(usuario.getGenero().name());
        dto.setAtivo(usuario.isAtivo());
        dto.setUserRole(usuario.getUserRole().name());
        return dto;
    }

}
