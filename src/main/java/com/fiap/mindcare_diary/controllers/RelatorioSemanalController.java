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

    @PostMapping("/gerar/{nomeUsuario}")
    public ResponseEntity<RelatorioSemanalDTO> gerarRelatorioSemanal(@PathVariable("nomeUsuario") String nomeUsuario, @RequestBody RelatorioSemanalDTO relatorioSemanal) {
        return ResponseEntity.ok(relatorioSemanalService.gerarRelatorioSemanal(nomeUsuario, relatorioSemanal));
    }

    @GetMapping("/{nomeUsuario}")
    public ResponseEntity<List<RelatorioSemanalDTO>> retornarRelatoriosSemanaisPorPaciente(@PathVariable("nomeUsuario") String nomeUsuario)  {
        return ResponseEntity.ok(relatorioSemanalService.retornarRelatoriosSemanaisPorPaciente(nomeUsuario));
    }
}
