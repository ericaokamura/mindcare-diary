package com.fiap.mindcare_diary.controllers;

import com.fiap.mindcare_diary.models.dtos.UsuarioDTO;
import com.fiap.mindcare_diary.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> retornarTodosUsuarios() {
        return ResponseEntity.ok(usuarioService.retornarTodosUsuarios());
    }

}
