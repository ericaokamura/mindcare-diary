package com.fiap.mindcare_diary.mappers;

import com.fiap.mindcare_diary.models.Usuario;
import com.fiap.mindcare_diary.models.dtos.UsuarioDTO;

import java.util.ArrayList;
import java.util.List;

public class UsuarioMapper {

    public static List<UsuarioDTO> convertListModelToListDTO(List<Usuario> usuarios) {
        List<UsuarioDTO> dtos = new ArrayList<>();
        for (Usuario usuario : usuarios) {
            UsuarioDTO dto = new UsuarioDTO();
            dto.setNomeUsuario(usuario.getNomeUsuario());
            dto.setTipoUsuario(usuario.getTipoUsuario().name());
            dto.setCpf(usuario.getCpf());
            dto.setNomeCompleto(usuario.getNomeCompleto());
            dto.setDataNascimento(usuario.getDataNascimento());
            dto.setEnderecoCompleto(usuario.getEnderecoCompleto());
            dto.setDataCriacaoCadastro(usuario.getDataCriacaoCadastro());
            dtos.add(dto);
        }
        return dtos;
    }
}
