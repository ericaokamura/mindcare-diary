package com.fiap.mindcare_diary.controllers;

import com.fiap.mindcare_diary.models.dtos.ClinicaDTO;
import com.fiap.mindcare_diary.models.dtos.ConsultaDTO;
import com.fiap.mindcare_diary.models.dtos.PacienteDTO;
import com.fiap.mindcare_diary.models.dtos.ProfissionalDTO;
import com.fiap.mindcare_diary.services.ClinicaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("clinicas")
@CrossOrigin(value = "*", allowedHeaders = "*")
@Tag(
        name = "Clínicas",
        description = "Gestão de dados de pacientes e profissionais da clínica."
)
public class ClinicaController {

    @Autowired
    private ClinicaService clinicaService;

    @Operation(
            summary = "Retorna clínica por id",
            description = "Retorna dados da clínica por id."
    )
    @GetMapping("/{clinicaId}")
    public ResponseEntity<ClinicaDTO> retornarClinicaPorId(@PathVariable("clinicaId") Long clinicaId) {
        return ResponseEntity.ok(clinicaService.retornarClinicaPorId(clinicaId));
    }

    @Operation(
            summary = "Retorna pacientes de clínica por id",
            description = "Retorna pacientes de clínica por id."
    )
    @GetMapping("/{clinicaId}/pacientes")
    public ResponseEntity<List<PacienteDTO>> retornarPacientesPorClinicaId(@PathVariable("clinicaId") Long clinicaId) {
        return ResponseEntity.ok(clinicaService.retornarPacientesPorClinicaId(clinicaId));
    }

    @Operation(
            summary = "Retorna profissionais de clínica por id",
            description = "Retorna profissionais de clínica por id."
    )
    @GetMapping("/{clinicaId}/profissionais")
    public ResponseEntity<List<ProfissionalDTO>> retornarProfissionaisPorClinicaId(@PathVariable("clinicaId") Long clinicaId) {
        return ResponseEntity.ok(clinicaService.retornarProfissionaisPorClinicaId(clinicaId));
    }

    @Operation(
            summary = "Retorna consultas de clínica por id",
            description = "Retorna consultas de clínica por id."
    )
    @GetMapping("/{clinicaId}/consultas")
    public ResponseEntity<List<ConsultaDTO>> retornarConsultasPorClinicaId(@PathVariable("clinicaId") Long clinicaId) {
        return ResponseEntity.ok(clinicaService.retornarConsultasPorClinicaId(clinicaId));
    }

    @Operation(
            summary = "Retorna faturamento da clínica por ano e mês",
            description = "Retorna faturamento da clínica antes do desconto de comissão por ano e mês."
    )
    @GetMapping("/{clinicaId}/faturamento")
    public ResponseEntity<Double> retornarFaturamentoPorClinicaIdPorAnoMes(@PathVariable("clinicaId") Long clinicaId, @RequestParam("ano") Long ano, @RequestParam("mes") Long mes) {
        return ResponseEntity.ok(clinicaService.retornarFaturamentoPorClinicaIdPorAnoMes(clinicaId, ano, mes));
    }

    @Operation(
            summary = "Retorna faturamento da clínica por ano e mês",
            description = "Retorna faturamento da clínica após desconto de comissão por ano e mês."
    )
    @GetMapping("/{clinicaId}/receita/descontos")
    public ResponseEntity<Double> retornarReceitaAposDescontosPorClinicaIdPorAnoMes(@PathVariable("clinicaId") Long clinicaId, @RequestParam("ano") Long ano, @RequestParam("mes") Long mes) {
        return ResponseEntity.ok(clinicaService.retornarReceitaAposDescontosPorClinicaIdPorAnoMes(clinicaId, ano, mes));
    }

    @Operation(
            summary = "Cadastra clínica",
            description = "Cadastra dados da clínica."
    )
    @PostMapping()
    public ResponseEntity<Void> cadastrarClinica(@RequestBody ClinicaDTO clinicaDTO) {
        clinicaService.cadastrarClinica(clinicaDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Atualiza clínica",
            description = "Atualiza dados da clínica."
    )
    @PatchMapping("{clinicaId}")
    public ResponseEntity<Void> atualizarDadosClinica(@PathVariable("clinicaId") Long clinicaId, @RequestBody ClinicaDTO clinicaDTO) {
        clinicaService.atualizarDadosClinica(clinicaId, clinicaDTO);
        return ResponseEntity.ok().build();
    }
}
