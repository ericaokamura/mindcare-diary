package com.fiap.mindcare_diary.controllers;

import com.fiap.mindcare_diary.models.dtos.ClinicaDTO;
import com.fiap.mindcare_diary.models.dtos.ConsultaDTO;
import com.fiap.mindcare_diary.services.ClinicaService;
import com.fiap.mindcare_diary.services.ConsultaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("consultas")
public class ConsultaController {

    @Autowired
    private ConsultaService consultaService;

    @GetMapping("/pacientes/{nomeUsuario}")
    public ResponseEntity<List<ConsultaDTO>> retornarConsultasPorPaciente(@PathVariable("nomeUsuario") String nomeUsuario) {
        return ResponseEntity.ok(consultaService.retornarConsultasPorPaciente(nomeUsuario));
    }

    @GetMapping("/profissionais/{nomeUsuario}")
    public ResponseEntity<List<ConsultaDTO>> retornarConsultasPorProfissional(@PathVariable("nomeUsuario") String nomeUsuario) {
        return ResponseEntity.ok(consultaService.retornarConsultasPorProfissional(nomeUsuario));
    }

    @PatchMapping("/{consultaId}")
    public ResponseEntity<Void> atualizarConsulta(@PathVariable("consultaId") Long consultaId, @RequestBody ConsultaDTO consultaDTO) {
        consultaService.atualizarConsulta(consultaId, consultaDTO);
        return ResponseEntity.ok().build();
    }
}
