package com.fiap.mindcare_diary.controllers;

import com.fiap.mindcare_diary.models.dtos.ClinicaDTO;
import com.fiap.mindcare_diary.models.dtos.ConsultaDTO;
import com.fiap.mindcare_diary.models.dtos.PacienteDTO;
import com.fiap.mindcare_diary.models.dtos.ProfissionalDTO;
import com.fiap.mindcare_diary.services.ClinicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("clinicas")
public class ClinicaController {

    @Autowired
    private ClinicaService clinicaService;

    @GetMapping("/{clinicaId}")
    public ResponseEntity<ClinicaDTO> retornarClinicaPorId(@PathVariable("clinicaId") Long clinicaId) {
        return ResponseEntity.ok(clinicaService.retornarClinicaPorId(clinicaId));
    }

    @GetMapping("/{clinicaId}/pacientes")
    public ResponseEntity<List<PacienteDTO>> retornarPacientesPorClinicaId(@PathVariable("clinicaId") Long clinicaId) {
        return ResponseEntity.ok(clinicaService.retornarPacientesPorClinicaId(clinicaId));
    }

    @GetMapping("/{clinicaId}/profissionais")
    public ResponseEntity<List<ProfissionalDTO>> retornarProfissionaisPorClinicaId(@PathVariable("clinicaId") Long clinicaId) {
        return ResponseEntity.ok(clinicaService.retornarProfissionaisPorClinicaId(clinicaId));
    }

    @GetMapping("/{clinicaId}/consultas")
    public ResponseEntity<List<ConsultaDTO>> retornarConsultasPorClinicaId(@PathVariable("clinicaId") Long clinicaId) {
        return ResponseEntity.ok(clinicaService.retornarConsultasPorClinicaId(clinicaId));
    }

    @GetMapping("/{clinicaId}/faturamento")
    public ResponseEntity<Double> retornarFaturamentoPorClinicaIdPorAnoMes(@PathVariable("clinicaId") Long clinicaId, @RequestParam("ano") Long ano, @RequestParam("mes") Long mes) {
        return ResponseEntity.ok(clinicaService.retornarFaturamentoPorClinicaIdPorAnoMes(clinicaId, ano, mes));
    }

    @GetMapping("/{clinicaId}/receita/descontos")
    public ResponseEntity<Double> retornarReceitaAposDescontosPorClinicaIdPorAnoMes(@PathVariable("clinicaId") Long clinicaId, @RequestParam("ano") Long ano, @RequestParam("mes") Long mes) {
        return ResponseEntity.ok(clinicaService.retornarReceitaAposDescontosPorClinicaIdPorAnoMes(clinicaId, ano, mes));
    }

    @PostMapping()
    public ResponseEntity<Void> cadastrarClinica(@RequestBody ClinicaDTO clinicaDTO) {
        clinicaService.cadastrarClinica(clinicaDTO);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("{clinicaId}")
    public ResponseEntity<Void> atualizarDadosClinica(@PathVariable("clinicaId") Long clinicaId, @RequestBody ClinicaDTO clinicaDTO) {
        clinicaService.atualizarDadosClinica(clinicaId, clinicaDTO);
        return ResponseEntity.ok().build();
    }
}
