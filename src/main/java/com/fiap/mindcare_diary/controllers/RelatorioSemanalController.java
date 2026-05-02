package com.fiap.mindcare_diary.controllers;

import com.fiap.mindcare_diary.models.dtos.RelatorioSemanalDTO;
import com.fiap.mindcare_diary.services.RelatorioSemanalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("relatoriosSemanais")
public class RelatorioSemanalController {

    @Autowired
    private RelatorioSemanalService relatorioSemanalService;

    @PostMapping("/gerar/{idPaciente}")
    public ResponseEntity<RelatorioSemanalDTO> gerarRelatorioSemanal(@PathVariable("idPaciente") Long idPaciente, @RequestBody RelatorioSemanalDTO relatorioSemanal) {
        return ResponseEntity.ok(relatorioSemanalService.gerarRelatorioSemanal(idPaciente, relatorioSemanal));
    }

    @GetMapping("/{idPaciente}")
    public ResponseEntity<List<RelatorioSemanalDTO>> retornarRelatoriosSemanaisPorPaciente(@PathVariable("idPaciente") Long idPaciente)  {
        return ResponseEntity.ok(relatorioSemanalService.retornarRelatoriosSemanaisPorPaciente(idPaciente));
    }
}
