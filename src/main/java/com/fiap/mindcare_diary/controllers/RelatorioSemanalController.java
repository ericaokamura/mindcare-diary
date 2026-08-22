package com.fiap.mindcare_diary.controllers;

import com.fiap.mindcare_diary.models.dtos.RegistroDiarioDTO;
import com.fiap.mindcare_diary.models.dtos.RelatorioSemanalDTO;
import com.fiap.mindcare_diary.services.RelatorioSemanalService;
import com.fiap.mindcare_diary.utils.DataLoader;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("relatoriosSemanais")
@CrossOrigin(value = "*", allowedHeaders = "*")
@Tag(
        name = "Relatórios Semanais",
        description = "Operações relacionadas a relatórios semanais."
)
public class RelatorioSemanalController {

    @Autowired
    private RelatorioSemanalService relatorioSemanalService;

    @Operation(
            summary = "Gera relatório semanal para paciente",
            description = "Gera relatório semanal para paciente."
    )
    @PostMapping("/gerar/{nomeUsuario}")
    public ResponseEntity<RelatorioSemanalDTO> gerarRelatorioSemanal(@PathVariable("nomeUsuario") String nomeUsuario) {
        return ResponseEntity.ok(relatorioSemanalService.gerarRelatorioSemanal(nomeUsuario));
    }

    @Operation(
            summary = "Retorna relatórios semanais por paciente",
            description = "Retorna relatórios semanais por paciente."
    )
    @GetMapping("/{nomeUsuario}")
    public ResponseEntity<List<RelatorioSemanalDTO>> retornarRelatoriosSemanaisPorPaciente(@PathVariable("nomeUsuario") String nomeUsuario)  {
        return ResponseEntity.ok(relatorioSemanalService.retornarRelatoriosSemanaisPorPaciente(nomeUsuario));
    }

    @Operation(
            summary = "Atualiza relatório semanal do paciente",
            description = "Atualiza relatório semanal do paciente."
    )
    @PatchMapping("/atualizarRelatorioSemanal")
    public ResponseEntity<Void> atualizarRelatorioSemanal(@RequestBody RelatorioSemanalDTO relatorioSemanalDTO) {
        relatorioSemanalService.atualizarRelatorioSemanal(relatorioSemanalDTO);
        return ResponseEntity.ok().build();
    }

}
