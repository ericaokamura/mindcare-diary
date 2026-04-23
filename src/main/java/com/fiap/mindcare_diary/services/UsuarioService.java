package com.fiap.mindcare_diary.services;

import com.fiap.mindcare_diary.mappers.UsuarioMapper;
import com.fiap.mindcare_diary.models.Usuario;
import com.fiap.mindcare_diary.models.dtos.UsuarioDTO;
import com.fiap.mindcare_diary.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<UsuarioDTO> retornarTodosUsuarios() {
        List<Usuario> usuarios = this.usuarioRepository.findAll();
        return UsuarioMapper.convertListModelToListDTO(usuarios);
    }
}
