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
            summary = "Retorna clínica por cnpj",
            description = "Retorna dados da clínica por cnpj."
    )
    @GetMapping("/{clinicaCnpj}/cnpj")
    public ResponseEntity<ClinicaDTO> retornarClinicaPorCnpj(@PathVariable("clinicaCnpj") String clinicaCnpj) {
        return ResponseEntity.ok(clinicaService.retornarClinicaPorCnpj(clinicaCnpj));
    }

    @Operation(
            summary = "Retorna clínica por nome",
            description = "Retorna dados da clínica por nome."
    )
    @GetMapping("/{nome}/nome")
    public ResponseEntity<ClinicaDTO> retornarClinicaPorNome(@PathVariable("nome") String nome) {
        return ResponseEntity.ok(clinicaService.retornarClinicaPorNome(nome));
    }

    @Operation(
            summary = "Retorna clínicas",
            description = "Retorna todas as clínicas."
    )
    @GetMapping()
    public ResponseEntity<List<ClinicaDTO>> retornarClinicas() {
        return ResponseEntity.ok(clinicaService.retornarClinicas());
    }

    @Operation(
            summary = "Retorna pacientes de clínica por cnpj",
            description = "Retorna pacientes de clínica por cnpj."
    )
    @GetMapping("/{clinicaCnpj}/pacientes")
    public ResponseEntity<List<PacienteDTO>> retornarPacientesPorclinicaCnpj(@PathVariable("clinicaCnpj") String clinicaCnpj) {
        return ResponseEntity.ok(clinicaService.retornarPacientesPorClinicaCnpj(clinicaCnpj));
    }

    @Operation(
            summary = "Retorna profissionais de clínica por cnpj",
            description = "Retorna profissionais de clínica por cnpj."
    )
    @GetMapping("/{clinicaCnpj}/profissionais")
    public ResponseEntity<List<ProfissionalDTO>> retornarProfissionaisPorclinicaCnpj(@PathVariable("clinicaCnpj") String clinicaCnpj) {
        return ResponseEntity.ok(clinicaService.retornarProfissionaisPorClinicaCnpj(clinicaCnpj));
    }

    @Operation(
            summary = "Retorna consultas de clínica por cnpj",
            description = "Retorna consultas de clínica por cnpj."
    )
    @GetMapping("/{clinicaCnpj}/consultas")
    public ResponseEntity<List<ConsultaDTO>> retornarConsultasPorclinicaCnpj(@PathVariable("clinicaCnpj") String clinicaCnpj) {
        return ResponseEntity.ok(clinicaService.retornarConsultasPorClinicaCnpj(clinicaCnpj));
    }

    @Operation(
            summary = "Retorna faturamento da clínica por ano e mês",
            description = "Retorna faturamento da clínica antes do desconto de comissão por ano e mês."
    )
    @GetMapping("/faturamento")
    public ResponseEntity<Double> retornarFaturamentoPorclinicaCnpjPorAnoMes(@RequestParam("clinicaCnpj") String clinicaCnpj, @RequestParam("ano") Long ano, @RequestParam("mes") Long mes) {
        return ResponseEntity.ok(clinicaService.retornarFaturamentoPorClinicaCnpjPorAnoMes(clinicaCnpj, ano, mes));
    }

    @Operation(
            summary = "Retorna faturamento da clínica por ano e mês",
            description = "Retorna faturamento da clínica após desconto de comissão por ano e mês."
    )
    @GetMapping("/receita/descontos")
    public ResponseEntity<Double> retornarReceitaAposDescontosPorclinicaCnpjPorAnoMes(@RequestParam("clinicaCnpj") String clinicaCnpj, @RequestParam("ano") Long ano, @RequestParam("mes") Long mes) {
        return ResponseEntity.ok(clinicaService.retornarReceitaAposDescontosPorClinicaCnpjPorAnoMes(clinicaCnpj, ano, mes));
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
    @PatchMapping("{clinicaCnpj}")
    public ResponseEntity<Void> atualizarDadosClinica(@PathVariable("clinicaCnpj") String clinicaCnpj, @RequestBody ClinicaDTO clinicaDTO) {
        clinicaService.atualizarDadosClinica(clinicaCnpj, clinicaDTO);
        return ResponseEntity.ok().build();
    }
}
