package com.fiap.mindcare_diary.controllers;

import com.fiap.mindcare_diary.models.RecomendacaoHorario;
import com.fiap.mindcare_diary.models.dtos.ConsultaDTO;
import com.fiap.mindcare_diary.services.AgendamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("agendamentos")
@CrossOrigin(value = "*", allowedHeaders = "*")
@Tag(
        name = "Agendamentos",
        description = "Gestão de agendamentos de consultas com psicólogo ou psiquiatra."
)
public class AgendamentoController {

    @Autowired
    private AgendamentoService agendamentoService;

    @Operation(
            summary = "Carregar consultas por paciente",
            description = "Carregar consultas por paciente."
    )
    @GetMapping("/{nomeUsuario}")
    public ResponseEntity<List<ConsultaDTO>> carregarConsultas(@PathVariable("nomeUsuario") String nomeUsuario) {
        List<ConsultaDTO> consultas = this.agendamentoService.carregarConsultas(nomeUsuario);
        return ResponseEntity.ok().body(consultas);
    }

    @Operation(
            summary = "Agenda consulta",
            description = "Agenda consulta para paciente."
    )
    @PostMapping
    public ResponseEntity<Void> agendarConsulta(@RequestBody ConsultaDTO consultaDTO) {
        this.agendamentoService.salvarAgendamento(consultaDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Recomenda horários de agendamento de consulta",
            description = "Recomenda horários de agendamento de consulta para paciente."
    )
    @GetMapping("/recomendarHorarios")
    public ResponseEntity<List<RecomendacaoHorario>> recomendarHorarios(@RequestParam("tipoProfissional") String tipoProfissional) {
        return ResponseEntity.ok(this.agendamentoService.recomendarHorariosParaSemanaCorrente(tipoProfissional));
    }

    @Operation(
            summary = "Recomenda horários de agendamento de consulta para profissional e data desejados",
            description = "Recomenda horários de agendamento de consulta para profissional e data desejados."
    )
    @GetMapping("/recomendarHorarios/{dataInformada}/profissional/{nomeUsuario}")
    public ResponseEntity<List<RecomendacaoHorario>> recomendarHorariosParaProfissionalEDataInformada(@PathVariable("dataInformada") String dataInformada, @PathVariable("nomeUsuario") String nomeUsuario) {
        return ResponseEntity.ok(this.agendamentoService.informarHorariosParaProfissionalEDataInformada(nomeUsuario, dataInformada));
    }
}
