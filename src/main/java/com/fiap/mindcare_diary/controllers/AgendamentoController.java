package com.fiap.mindcare_diary.controllers;

import com.fiap.mindcare_diary.models.RecomendacaoHorario;
import com.fiap.mindcare_diary.models.dtos.ConsultaDTO;
import com.fiap.mindcare_diary.services.AgendamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("agendamentos")
@CrossOrigin(value = "*", allowedHeaders = "*")
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
