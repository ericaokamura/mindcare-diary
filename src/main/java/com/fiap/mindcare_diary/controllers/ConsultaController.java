package com.fiap.mindcare_diary.controllers;

import com.fiap.mindcare_diary.models.dtos.ClinicaDTO;
import com.fiap.mindcare_diary.models.dtos.ConsultaDTO;
import com.fiap.mindcare_diary.services.ClinicaService;
import com.fiap.mindcare_diary.services.ConsultaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("consultas")
@CrossOrigin(value = "*", allowedHeaders = "*")
@Tag(
        name = "Consultas",
        description = "Gestão de consultas agendadas ou canceladas."
)
public class ConsultaController {

    @Autowired
    private ConsultaService consultaService;

    @Operation(
            summary = "Retorna consultas por paciente",
            description = "Retorna consultas por paciente."
    )
    @GetMapping("/pacientes/{nomeUsuario}")
    public ResponseEntity<List<ConsultaDTO>> retornarConsultasPorPaciente(@PathVariable("nomeUsuario") String nomeUsuario) {
        return ResponseEntity.ok(consultaService.retornarConsultasPorPaciente(nomeUsuario));
    }

    @Operation(
            summary = "Retorna consultas por profissional",
            description = "Retorna consultas por profissional."
    )
    @GetMapping("/profissionais/{nomeUsuario}")
    public ResponseEntity<List<ConsultaDTO>> retornarConsultasPorProfissional(@PathVariable("nomeUsuario") String nomeUsuario) {
        return ResponseEntity.ok(consultaService.retornarConsultasPorProfissional(nomeUsuario));
    }

    @Operation(
            summary = "Atualiza consulta",
            description = "Atualiza dados de consulta."
    )
    @PatchMapping("/{consultaId}")
    public ResponseEntity<Void> atualizarConsulta(@PathVariable("consultaId") Long consultaId, @RequestBody ConsultaDTO consultaDTO) {
        consultaService.atualizarConsulta(consultaId, consultaDTO);
        return ResponseEntity.ok().build();
    }
}
