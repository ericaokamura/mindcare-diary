package com.fiap.mindcare_diary.controllers;

import com.fiap.mindcare_diary.models.AuditoriaLog;
import com.fiap.mindcare_diary.models.Profissional;
import com.fiap.mindcare_diary.models.dtos.PacienteDTO;
import com.fiap.mindcare_diary.models.dtos.PrescriptionDTO;
import com.fiap.mindcare_diary.models.dtos.ProfissionalDTO;
import com.fiap.mindcare_diary.models.enums.AuditAction;
import com.fiap.mindcare_diary.services.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("prescriptions")
public class PrescriptionController {

    @Autowired
    private PrescricaoService prescricaoService;

    @Autowired
    private AuditoriaLogService auditoriaLogService;

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private ProfissionalService profissionalService;

    @Autowired
    private TokenService tokenService;

    @PreAuthorize("hasAuthority('PROFESSIONAL_UPLOAD_PRESCRIPTION')")
    @PostMapping(value = "/{pacienteNomeUsuario}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> salvarPrescricaoDePaciente(@PathVariable("pacienteNomeUsuario") String pacienteNomeUsuario,
                                                           HttpServletRequest request,
                                                           @RequestParam String issueDate,
                                                           @RequestParam String expirationDate,
                                                           @RequestParam String medicines,
                                                           @RequestParam boolean controlled,
                                                           @RequestPart("arquivo") MultipartFile arquivo) throws IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            authorizationHeader = authorizationHeader.replace("Bearer ", "");
        }
        String suject = tokenService.getSubject(authorizationHeader);
        ProfissionalDTO profissionalDTO = profissionalService.retornarProfissional(suject);
        AuditoriaLog auditoriaLog = new AuditoriaLog();
        auditoriaLog.setMensagem("Dados do paciente: (nomeUsuario: " + profissionalDTO.getNomeUsuario() + "), dados do profissional: (nomeUsuario: " + profissionalDTO.getNomeUsuario() + ")");
        auditoriaLog.setHttpMethod(HttpMethod.POST);
        auditoriaLog.setDataHoraAuditoria(LocalDateTime.now());
        auditoriaLog.setAction(AuditAction.PRESCRIPTION_UPLOAD);
        auditoriaLogService.salvarLog(auditoriaLog);

        pacienteService.salvarPrescricaoDePaciente(pacienteNomeUsuario, profissionalDTO.getNomeUsuario(), issueDate, expirationDate, medicines, controlled, arquivo);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('PATIENT_DOWNLOAD_PRESCRIPTION')")
    @GetMapping(value = "/{profissionalNomeUsuario}/{number}/pdf", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> baixarPdf(@PathVariable("profissionalNomeUsuario") String profissionalNomeUsuario, @PathVariable String number, HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            authorizationHeader = authorizationHeader.replace("Bearer ", "");
        }
        String suject = tokenService.getSubject(authorizationHeader);
        PacienteDTO pacienteDTO = pacienteService.retornarCadastroPaciente(suject);

        AuditoriaLog auditoriaLog = new AuditoriaLog();
        auditoriaLog.setMensagem("Dados do paciente: (nomeUsuario: " + pacienteDTO.getNomeUsuario() + ")");
        auditoriaLog.setHttpMethod(HttpMethod.GET);
        auditoriaLog.setDataHoraAuditoria(LocalDateTime.now());
        auditoriaLog.setAction(AuditAction.PRESCRIPTION_DOWNLOAD);
        auditoriaLogService.salvarLog(auditoriaLog);

        PrescriptionDTO receita = prescricaoService.retornarPrescricaoPorNumber(pacienteDTO.getNomeUsuario(), number);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).header(HttpHeaders.CONTENT_DISPOSITION,
                "inline; filename=\"" + receita.getPrescriptionDocument().getNomeArquivo() + "\"").body(receita.getPrescriptionDocument().getArquivoPdf());
    }
}
