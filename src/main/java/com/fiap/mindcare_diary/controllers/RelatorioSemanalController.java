package com.fiap.mindcare_diary.controllers;

import com.fiap.mindcare_diary.models.dtos.RegistroDiarioDTO;
import com.fiap.mindcare_diary.models.dtos.RelatorioSemanalDTO;
import com.fiap.mindcare_diary.services.RelatorioSemanalService;
import com.fiap.mindcare_diary.utils.DataLoader;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
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
    public ResponseEntity<RelatorioSemanalDTO> gerarRelatorioSemanal(@PathVariable("nomeUsuario") String nomeUsuario) {
        return ResponseEntity.ok(relatorioSemanalService.gerarRelatorioSemanal(nomeUsuario));
    }

    @GetMapping("/{nomeUsuario}")
    public ResponseEntity<List<RelatorioSemanalDTO>> retornarRelatoriosSemanaisPorPaciente(@PathVariable("nomeUsuario") String nomeUsuario)  {
        return ResponseEntity.ok(relatorioSemanalService.retornarRelatoriosSemanaisPorPaciente(nomeUsuario));
    }

    @PatchMapping("/atualizarRelatorioSemanal")
    public ResponseEntity<Void> atualizarRelatorioSemanal(@RequestBody RelatorioSemanalDTO relatorioSemanalDTO) {
        relatorioSemanalService.atualizarRelatorioSemanal(relatorioSemanalDTO);
        return ResponseEntity.ok().build();
    }

}
