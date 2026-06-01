package com.fiap.mindcare_diary.controllers;

import com.fiap.mindcare_diary.models.RecomendacaoHorario;
import com.fiap.mindcare_diary.models.dtos.ConsultaDTO;
import com.fiap.mindcare_diary.models.enums.TipoProfissional;
import com.fiap.mindcare_diary.services.AgendamentoService;
import com.fiap.mindcare_diary.utils.DataLoader;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("agendamentos")
public class AgendamentoController {

    @Autowired
    private AgendamentoService agendamentoService;

    @PostMapping
    public ResponseEntity<Void> agendarConsulta(@RequestBody ConsultaDTO consultaDTO) {
        this.agendamentoService.salvarAgendamento(consultaDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/recomendarHorarios")
    public ResponseEntity<List<RecomendacaoHorario>> recomendarHorarios(@RequestParam("tipoProfissional") String tipoProfissional) {
        return ResponseEntity.ok(this.agendamentoService.recomendarHorariosParaSemanaCorrente(tipoProfissional));
    }

    @GetMapping("/recomendarHorarios/{dataInformada}/profissional/{nomeUsuario}")
    public ResponseEntity<List<RecomendacaoHorario>> recomendarHorariosParaProfissionalEDataInformada(@PathVariable("dataInformada") String dataInformada, @PathVariable("nomeUsuario") String nomeUsuario) {
        return ResponseEntity.ok(this.agendamentoService.informarHorariosParaProfissionalEDataInformada(nomeUsuario, dataInformada));
    }
}
