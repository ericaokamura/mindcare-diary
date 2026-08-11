package com.fiap.mindcare_diary.controllers;

import com.fiap.mindcare_diary.models.dtos.PacienteDTO;
import com.fiap.mindcare_diary.models.dtos.PrescriptionDTO;
import com.fiap.mindcare_diary.services.PacienteService;
import com.fiap.mindcare_diary.services.PrescricaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("pacientes")
@CrossOrigin(value = "*", allowedHeaders = "*")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private PrescricaoService prescricaoService;

    @PostMapping()
    public ResponseEntity<Void> salvarCadastroPaciente(@RequestBody PacienteDTO pacienteDTO) {
        pacienteService.salvarCadastroPaciente(pacienteDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{nomeUsuario}")
    public ResponseEntity<PacienteDTO> retornarCadastroPaciente(@PathVariable("nomeUsuario") String nomeUsuario) {
        return ResponseEntity.ok(pacienteService.retornarCadastroPaciente(nomeUsuario));
    }

    @PatchMapping("/selecionarProfissional/{idProfissional}/{idPaciente}")
    public ResponseEntity<PacienteDTO> selecionarProfissional(@PathVariable("idProfissional") Long idProfissional, @PathVariable("idPaciente") Long idPaciente) {
        return ResponseEntity.ok(pacienteService.selecionarProfissional(idProfissional, idPaciente));
    }

    @PatchMapping("/atualizarEstadoPaciente/{idProfissional}/{idPaciente}")
    public ResponseEntity<PacienteDTO> atualizarEstadoPaciente(@PathVariable("idProfissional") Long idProfissional, @PathVariable("idPaciente") Long idPaciente, @RequestParam("estadoPaciente") String estadoPaciente) {
        return ResponseEntity.ok(pacienteService.atualizarEstadoPaciente(idProfissional, idPaciente, estadoPaciente));
    }

    @GetMapping("/{nomeUsuario}/prescriptions")
    public ResponseEntity<List<PrescriptionDTO>> retornarPrescricoes(@PathVariable("nomeUsuario") String nomeUsuario) {
        return ResponseEntity.ok(pacienteService.retornarPrescricoes(nomeUsuario));
    }

    @PostMapping(value = "/{nomeUsuario}/prescriptions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> salvarPrescricaoDePaciente(@PathVariable("nomeUsuario") String nomeUsuario,
                                                           @RequestParam String profissionalNomeUsuario,
                                                           @RequestParam String issueDate,
                                                           @RequestParam String expirationDate,
                                                           @RequestParam String medicines,
                                                           @RequestParam boolean controlled,
                                                           @RequestPart("arquivo") MultipartFile arquivo) throws IOException {
        pacienteService.salvarPrescricaoDePaciente(nomeUsuario, profissionalNomeUsuario, issueDate, expirationDate, medicines, controlled, arquivo);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{nomeUsuario}/prescriptions/{number}/pdf")
    public ResponseEntity<byte[]> baixarPdf(@PathVariable String number) {
        PrescriptionDTO receita = prescricaoService.retornarPrescricaoPorNumber(number);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + receita.getNomeArquivo() + "\"").body(receita.getArquivoPdf());
    }
}
