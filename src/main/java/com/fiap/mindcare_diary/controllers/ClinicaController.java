package com.fiap.mindcare_diary.controllers;

import com.fiap.mindcare_diary.models.Usuario;
import com.fiap.mindcare_diary.models.dtos.ClinicaDTO;
import com.fiap.mindcare_diary.models.dtos.ConsultaDTO;
import com.fiap.mindcare_diary.models.dtos.PacienteDTO;
import com.fiap.mindcare_diary.models.dtos.ProfissionalDTO;
import com.fiap.mindcare_diary.services.ClinicaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    @GetMapping("/{nomeClinica}/nome")
    public ResponseEntity<ClinicaDTO> retornarClinicaPorNome(@PathVariable("nomeClinica") String nomeClinica) {
        return ResponseEntity.ok(clinicaService.retornarClinicaPorNome(nomeClinica));
    }

    @Operation(
            summary = "Retorna a clínica de um admin",
            description = "Retorna a clínica associada ao usuário admin informado, ou 404 se ele ainda não tiver nenhuma."
    )
    @GetMapping("/admin/{nomeUsuario}")
    public ResponseEntity<ClinicaDTO> retornarClinicaPorAdmin(@PathVariable("nomeUsuario") String nomeUsuario) {
        ClinicaDTO clinicaDTO = clinicaService.retornarClinicaPorAdmin(nomeUsuario);
        if (clinicaDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(clinicaDTO);
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
    @GetMapping("/{nomeClinica}/consultas")
    public ResponseEntity<List<ConsultaDTO>> retornarConsultasPorNomeClinica(@PathVariable("nomeClinica") String nomeClinica) {
        return ResponseEntity.ok(clinicaService.retornarConsultasPorNomeClinica(nomeClinica));
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
            description = "Cadastra dados da clínica associando-a ao admin autenticado. Falha se o admin já tiver uma clínica."
    )
    @PreAuthorize("hasAuthority('USER_CREATE')")
    @PostMapping()
    public ResponseEntity<Void> cadastrarClinica(@RequestBody ClinicaDTO clinicaDTO) {
        Optional<Usuario> optionalAdmin = (Optional<Usuario>) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (optionalAdmin.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        clinicaService.cadastrarClinica(clinicaDTO, optionalAdmin.get());
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
