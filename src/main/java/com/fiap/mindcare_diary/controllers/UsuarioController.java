package com.fiap.mindcare_diary.controllers;

import com.fiap.mindcare_diary.models.dtos.UsuarioDTO;
import com.fiap.mindcare_diary.services.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("usuarios")
@CrossOrigin(value = "*", allowedHeaders = "*")
@Tag(
        name = "Usuários",
        description = "Operações relacionadas a usuários."
)
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Operation(
            summary = "Retorna todos os usuários",
            description = "Retorna todos os usuários."
    )
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> retornarTodosUsuarios() {
        return ResponseEntity.ok(usuarioService.retornarTodosUsuarios());
    }

    @Operation(
            summary = "Salvar token de usuário",
            description = "Salvar token de usuário."
    )
    @PostMapping("/token/{nomeUsuario}")
    public ResponseEntity<Void> salvarToken(@PathVariable("nomeUsuario") String nomeUsuario, @RequestParam("token") String token) {
        usuarioService.salvarToken(nomeUsuario, token);
        return ResponseEntity.ok().build();
    }

}
