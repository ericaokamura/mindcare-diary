package com.fiap.mindcare_diary.controllers;

import com.fiap.mindcare_diary.models.dtos.PacienteDTO;
import com.fiap.mindcare_diary.models.dtos.PrescriptionDTO;
import com.fiap.mindcare_diary.services.PacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("pacientes")
@CrossOrigin(value = "*", allowedHeaders = "*")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @PostMapping()
    public ResponseEntity<Void> salvarCadastroPaciente(@RequestBody PacienteDTO pacienteDTO) {
        pacienteService.salvarCadastroPaciente(pacienteDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{nomeUsuario}")
    public ResponseEntity<PacienteDTO> retornarCadastroPaciente(@PathVariable("nomeUsuario") String nomeUsuario) {
        return ResponseEntity.ok(pacienteService.retornarCadastroPaciente(nomeUsuario));
    }

    @PatchMapping("/selecionarProfissional/{profissionalNomeUsuario}/{pacienteNomeUsuario}")
    public ResponseEntity<PacienteDTO> selecionarProfissional(@PathVariable("profissionalNomeUsuario") String profissionalNomeUsuario, @PathVariable("pacienteNomeUsuario") String pacienteNomeUsuario) {
        return ResponseEntity.ok(pacienteService.selecionarProfissional(profissionalNomeUsuario, pacienteNomeUsuario));
    }

    @PatchMapping("/atualizarEstadoPaciente/{idProfissional}/{idPaciente}")
    public ResponseEntity<PacienteDTO> atualizarEstadoPaciente(@PathVariable("idProfissional") Long idProfissional, @PathVariable("idPaciente") Long idPaciente, @RequestParam("estadoPaciente") String estadoPaciente) {
        return ResponseEntity.ok(pacienteService.atualizarEstadoPaciente(idProfissional, idPaciente, estadoPaciente));
    }

    @GetMapping("/{nomeUsuario}/prescriptions")
    public ResponseEntity<List<PrescriptionDTO>> retornarPrescricoes(@PathVariable("nomeUsuario") String nomeUsuario) {
        return ResponseEntity.ok(pacienteService.retornarPrescricoes(nomeUsuario));
    }



}
