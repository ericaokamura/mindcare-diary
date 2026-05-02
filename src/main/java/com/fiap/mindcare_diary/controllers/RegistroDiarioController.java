package com.fiap.mindcare_diary.controllers;

import com.fiap.mindcare_diary.models.dtos.RegistroDiarioDTO;
import com.fiap.mindcare_diary.services.RegistroDiarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("registrosDiarios")
public class RegistroDiarioController {

    @Autowired
    private RegistroDiarioService registroDiarioService;

    @GetMapping("/{idPaciente}")
    public ResponseEntity<List<RegistroDiarioDTO>> retornarRegistrosDiarios(@PathVariable("idPaciente") Long idPaciente) {
        return ResponseEntity.ok(registroDiarioService.retornarRegistrosDiarios(idPaciente));
    }

    @PostMapping("/{idPaciente}")
    public ResponseEntity<Void> salvarRegistroDiario(@PathVariable("idPaciente") Long idPaciente, @RequestBody RegistroDiarioDTO registroDiarioDTO) {
        registroDiarioService.salvarRegistroDiario(idPaciente, registroDiarioDTO);
        return ResponseEntity.ok().build();
    }
}
