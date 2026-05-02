package com.fiap.mindcare_diary.controllers;

import com.fiap.mindcare_diary.models.dtos.RelatorioSemanalDTO;
import com.fiap.mindcare_diary.services.RelatorioSemanalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("relatoriosSemanais")
public class RelatorioSemanalController {

    @Autowired
    private RelatorioSemanalService relatorioSemanalService;

    @GetMapping("/gerar/{idPaciente}")
    public ResponseEntity<List<RelatorioSemanalDTO>> gerarRelatoriosSemanais(@PathVariable("idPaciente") Long idPaciente) {
        return ResponseEntity.ok(relatorioSemanalService.gerarRelatoriosSemanais(idPaciente));
    }
}
