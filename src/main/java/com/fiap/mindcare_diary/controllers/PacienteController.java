package com.fiap.mindcare_diary.controllers;

import com.fiap.mindcare_diary.models.dtos.PacienteDTO;
import com.fiap.mindcare_diary.models.dtos.PrescriptionDTO;
import com.fiap.mindcare_diary.services.PacienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("pacientes")
@CrossOrigin(value = "*", allowedHeaders = "*")
@Tag(
        name = "Pacientes",
        description = "Operações relacionadas aos pacientes"
)
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @Operation(
            summary = "Cadastra um paciente",
            description = "Cadastra os dados de um paciente."
    )
    @PostMapping()
    public ResponseEntity<Void> salvarCadastroPaciente(@RequestBody PacienteDTO pacienteDTO) {
        pacienteService.salvarCadastroPaciente(pacienteDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Retorna um paciente",
            description = "Retorna os dados de um paciente por nome de usuário."
    )
    @GetMapping("/{nomeUsuario}")
    public ResponseEntity<PacienteDTO> retornarCadastroPaciente(@PathVariable("nomeUsuario") String nomeUsuario) {
        return ResponseEntity.ok(pacienteService.retornarCadastroPaciente(nomeUsuario));
    }

    @Operation(
            summary = "Adiciona um profissional a um paciente",
            description = "Adiciona um profissional à lista de profissionais de um paciente."
    )
    @PatchMapping("/selecionarProfissional/{profissionalNomeUsuario}/{pacienteNomeUsuario}")
    public ResponseEntity<PacienteDTO> selecionarProfissional(@PathVariable("profissionalNomeUsuario") String profissionalNomeUsuario, @PathVariable("pacienteNomeUsuario") String pacienteNomeUsuario) {
        return ResponseEntity.ok(pacienteService.selecionarProfissional(profissionalNomeUsuario, pacienteNomeUsuario));
    }

    @Operation(
            summary = "Atualiza estado do paciente",
            description = "Atualiza estado do paciente."
    )
    @PatchMapping("/atualizarEstadoPaciente/{profissionalNomeUsuario}/{pacienteNomeUsuario}")
    public ResponseEntity<PacienteDTO> atualizarEstadoPaciente(@PathVariable("profissionalNomeUsuario") String profissionalNomeUsuario, @PathVariable("pacienteNomeUsuario") String pacienteNomeUsuario, @RequestParam("estadoPaciente") String estadoPaciente) {
        return ResponseEntity.ok(pacienteService.atualizarEstadoPaciente(profissionalNomeUsuario, pacienteNomeUsuario, estadoPaciente));
    }

    @Operation(
            summary = "Retorna receitas médicas de um paciente",
            description = "Retorna receitas médicas de um paciente passando o nome do usuário."
    )
    @GetMapping("/{nomeUsuario}/prescriptions")
    public ResponseEntity<List<PrescriptionDTO>> retornarPrescricoes(@PathVariable("nomeUsuario") String nomeUsuario) {
        return ResponseEntity.ok(pacienteService.retornarPrescricoes(nomeUsuario));
    }



}
