package com.fiap.mindcare_diary.controllers;

import com.fiap.mindcare_diary.models.dtos.PacienteDTO;
import com.fiap.mindcare_diary.services.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("pacientes")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @PostMapping()
    public ResponseEntity<Void> salvarCadastroPaciente(@RequestBody PacienteDTO pacienteDTO) {
        pacienteService.salvarCadastroPaciente(pacienteDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{idPaciente}")
    public ResponseEntity<PacienteDTO> retornarCadastroPaciente(@PathVariable("idPaciente") Long idPaciente) {
        return ResponseEntity.ok(pacienteService.retornarCadastroPaciente(idPaciente));
    }

    @PatchMapping("/selecionarProfissional/{idProfissional}/{idPaciente}")
    public ResponseEntity<PacienteDTO> selecionarProfissional(@PathVariable("idProfissional") Long idProfissional, @PathVariable("idPaciente") Long idPaciente) {
        return ResponseEntity.ok(pacienteService.selecionarProfissional(idProfissional, idPaciente));
    }

    @PatchMapping("/atualizarEstadoPaciente/{idProfissional}/{idPaciente}")
    public ResponseEntity<PacienteDTO> atualizarEstadoPaciente(@PathVariable("idProfissional") Long idProfissional, @PathVariable("idPaciente") Long idPaciente, @RequestParam("estadoPaciente") String estadoPaciente) {
        return ResponseEntity.ok(pacienteService.atualizarEstadoPaciente(idProfissional, idPaciente, estadoPaciente));
    }
}
