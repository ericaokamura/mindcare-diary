package com.fiap.mindcare_diary.services;

import com.fiap.mindcare_diary.exceptions.UsuarioNaoEncontradoException;
import com.fiap.mindcare_diary.mappers.UsuarioMapper;
import com.fiap.mindcare_diary.models.Usuario;
import com.fiap.mindcare_diary.models.dtos.UsuarioDTO;
import com.fiap.mindcare_diary.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<UsuarioDTO> retornarTodosUsuarios() {
        List<Usuario> usuarios = this.usuarioRepository.findAll();
        return UsuarioMapper.convertListModelToListDTO(usuarios);
    }

    public void salvarToken(String nomeUsuario, String token) {
        Optional<Usuario> usuarioOptional = this.usuarioRepository.findByNomeUsuario(nomeUsuario);
        if(usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            usuario.setToken(token);
            this.usuarioRepository.save(usuario);
        } else {
            throw new UsuarioNaoEncontradoException("Usuário não encontrado.");
        }
    }
}
