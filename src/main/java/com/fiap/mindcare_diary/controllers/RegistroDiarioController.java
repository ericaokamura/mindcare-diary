package com.fiap.mindcare_diary.controllers;

import com.fiap.mindcare_diary.models.dtos.RegistroDiarioDTO;
import com.fiap.mindcare_diary.services.RegistroDiarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("registrosDiarios")
@CrossOrigin(value = "*", allowedHeaders = "*")
@Tag(
        name = "Registros Diários",
        description = "Operações relacionadas a registros diários."
)
public class RegistroDiarioController {

    @Autowired
    private RegistroDiarioService registroDiarioService;

    @Operation(
            summary = "Retorna registros diários por paciente",
            description = "Retorna registros diários por paciente."
    )
    @GetMapping("/{nomeUsuario}")
    public ResponseEntity<List<RegistroDiarioDTO>> retornarRegistrosDiarios(@PathVariable("nomeUsuario") String nomeUsuario) {
        return ResponseEntity.ok(registroDiarioService.retornarRegistrosDiarios(nomeUsuario));
    }

    @Operation(
            summary = "Cadastra registro diário de paciente",
            description = "Cadastra registro diário de paciente."
    )
    @PostMapping("/cadastrarRegistroDiario/{nomeUsuario}")
    public ResponseEntity<Void> salvarRegistroDiario(@PathVariable("nomeUsuario") String nomeUsuario, @RequestBody RegistroDiarioDTO registroDiarioDTO) {
        registroDiarioService.salvarRegistroDiario(nomeUsuario, registroDiarioDTO);
        return ResponseEntity.ok().build();
    }
}
