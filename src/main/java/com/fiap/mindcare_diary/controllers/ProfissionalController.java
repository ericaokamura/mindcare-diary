package com.fiap.mindcare_diary.controllers;

import com.fiap.mindcare_diary.models.dtos.PacienteDTO;
import com.fiap.mindcare_diary.models.dtos.ProfissionalDTO;
import com.fiap.mindcare_diary.services.ProfissionalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("profissionais")
@CrossOrigin(value = "*", allowedHeaders = "*")
@Tag(
        name = "Profissionais",
        description = "Operações relacionadas a profissionais da saúde."
)
public class ProfissionalController {

    @Autowired
    private ProfissionalService profissionalService;

    @Operation(
            summary = "Cadastra profissional",
            description = "Cadastra dados de profissional."
    )
    @PostMapping()
    public ResponseEntity<Void> salvarCadastroProfissional(@RequestBody ProfissionalDTO profissionalDTO) {
        profissionalService.salvarCadastroProfissional(profissionalDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Atualiza profissional",
            description = "Atualiza dados de profissional."
    )
    @PatchMapping("/{nomeUsuario}")
    public ResponseEntity<Void> atualizarDadosProfissional(@PathVariable("nomeUsuario") String nomeUsuario, @RequestBody ProfissionalDTO profissionalDTO) {
        profissionalService.atualizarDadosProfissional(nomeUsuario, profissionalDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Retorna pacientes de profissional",
            description = "Retorna dados de pacientes de profissional."
    )
    @GetMapping("/{nomeUsuario}/pacientes")
    public ResponseEntity<List<PacienteDTO>> retornarPacientesPorProfissional(@PathVariable("nomeUsuario") String nomeUsuario) {
        return ResponseEntity.ok(profissionalService.retornarPacientesPorProfissional(nomeUsuario));
    }

    @Operation(
            summary = "Retorna todos os profissionais cadastrados",
            description = "Retorna todos os profissionais cadastrados na plataforma MindCare Diary."
    )
    @GetMapping()
    public ResponseEntity<List<ProfissionalDTO>> retornarProfissionais() {
        return ResponseEntity.ok(profissionalService.retornarProfissionais());
    }

    @Operation(
            summary = "Retorna profissional por nome de usuário",
            description = "Retorna dados de profissional por nome de usuário."
    )
    @GetMapping("/{nomeUsuario}")
    public ResponseEntity<ProfissionalDTO> retornarProfissional(@PathVariable("nomeUsuario") String nomeUsuario) {
        return ResponseEntity.ok(profissionalService.retornarProfissional(nomeUsuario));
    }

    @Operation(
            summary = "Busca profissional por tipo",
            description = "Busca profissional por tipo."
    )
    @GetMapping("/tipoProfissional/{tipoProfissional}")
    public ResponseEntity<List<ProfissionalDTO>> buscarProfissionaisPorTipo(@PathVariable("tipoProfissional") String tipoProfissional) {
        return ResponseEntity.ok(profissionalService.buscarProfissionaisPorTipo(tipoProfissional));
    }

}
