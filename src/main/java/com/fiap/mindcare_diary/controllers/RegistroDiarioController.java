package com.fiap.mindcare_diary.controllers;

import com.fiap.mindcare_diary.models.dtos.RegistroDiarioDTO;
import com.fiap.mindcare_diary.services.RegistroDiarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("registrosDiarios")
@CrossOrigin(value = "*", allowedHeaders = "*")
public class RegistroDiarioController {

    @Autowired
    private RegistroDiarioService registroDiarioService;

    @GetMapping("/{nomeUsuario}")
    public ResponseEntity<List<RegistroDiarioDTO>> retornarRegistrosDiarios(@PathVariable("nomeUsuario") String nomeUsuario) {
        return ResponseEntity.ok(registroDiarioService.retornarRegistrosDiarios(nomeUsuario));
    }

    @PostMapping("/cadastrarRegistroDiario/{nomeUsuario}")
    public ResponseEntity<Void> salvarRegistroDiario(@PathVariable("nomeUsuario") String nomeUsuario, @RequestBody RegistroDiarioDTO registroDiarioDTO) {
        registroDiarioService.salvarRegistroDiario(nomeUsuario, registroDiarioDTO);
        return ResponseEntity.ok().build();
    }
}
