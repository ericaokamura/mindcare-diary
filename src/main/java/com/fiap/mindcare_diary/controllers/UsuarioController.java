package com.fiap.mindcare_diary.controllers;

import com.fiap.mindcare_diary.models.dtos.UsuarioDTO;
import com.fiap.mindcare_diary.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("usuarios")
@CrossOrigin(value = "*", allowedHeaders = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> retornarTodosUsuarios() {
        return ResponseEntity.ok(usuarioService.retornarTodosUsuarios());
    }

    @PostMapping("/token/{nomeUsuario}")
    public ResponseEntity<Void> salvarToken(@PathVariable("nomeUsuario") String nomeUsuario, @RequestParam("token") String token) {
        usuarioService.salvarToken(nomeUsuario, token);
        return ResponseEntity.ok().build();
    }

}
