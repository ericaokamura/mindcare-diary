package com.fiap.mindcare_diary.controllers;

import com.fiap.mindcare_diary.models.Profissional;
import com.fiap.mindcare_diary.models.dtos.PacienteDTO;
import com.fiap.mindcare_diary.models.dtos.ProfissionalDTO;
import com.fiap.mindcare_diary.services.PacienteService;
import com.fiap.mindcare_diary.services.ProfissionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.PrimitiveIterator;

@RestController
@RequestMapping("profissionais")
@CrossOrigin(value = "*", allowedHeaders = "*")
public class ProfissionalController {

    @Autowired
    private ProfissionalService profissionalService;

    @PostMapping()
    public ResponseEntity<Void> salvarCadastroProfissional(@RequestBody ProfissionalDTO profissionalDTO) {
        profissionalService.salvarCadastroProfissional(profissionalDTO);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{nomeUsuario}")
    public ResponseEntity<Void> atualizarDadosProfissional(@PathVariable("nomeUsuario") String nomeUsuario, @RequestBody ProfissionalDTO profissionalDTO) {
        profissionalService.atualizarDadosProfissional(nomeUsuario, profissionalDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{nomeUsuario}/pacientes")
    public ResponseEntity<List<PacienteDTO>> retornarPacientesPorProfissional(@PathVariable("nomeUsuario") String nomeUsuario) {
        return ResponseEntity.ok(profissionalService.retornarPacientesPorProfissional(nomeUsuario));
    }

    @GetMapping()
    public ResponseEntity<List<ProfissionalDTO>> retornarProfissionais() {
        return ResponseEntity.ok(profissionalService.retornarProfissionais());
    }

    @GetMapping("/{nomeUsuario}")
    public ResponseEntity<ProfissionalDTO> retornarProfissional(@PathVariable("nomeUsuario") String nomeUsuario) {
        return ResponseEntity.ok(profissionalService.retornarProfissional(nomeUsuario));
    }

    @GetMapping("/tipoProfissional/{tipoProfissional}")
    public ResponseEntity<List<ProfissionalDTO>> buscarProfissionaisProTipo(@PathVariable("tipoProfissional") String tipoProfissional) {
        return ResponseEntity.ok(profissionalService.buscarProfissionaisProTipo(tipoProfissional));
    }
}
