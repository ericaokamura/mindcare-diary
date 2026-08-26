package com.fiap.mindcare_diary.controllers;

import com.fiap.mindcare_diary.models.Paciente;
import com.fiap.mindcare_diary.models.Profissional;
import com.fiap.mindcare_diary.models.Usuario;
import com.fiap.mindcare_diary.models.dtos.PacienteDTO;
import com.fiap.mindcare_diary.models.dtos.PrescriptionDTO;
import com.fiap.mindcare_diary.models.dtos.ProfissionalDTO;
import com.fiap.mindcare_diary.models.enums.AuditAction;
import com.fiap.mindcare_diary.models.enums.Authority;
import com.fiap.mindcare_diary.models.enums.TipoProfissional;
import com.fiap.mindcare_diary.services.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("prescriptions")
@CrossOrigin(value = "*", allowedHeaders = "*")
@Tag(
        name = "Receitas Médicas",
        description = "Operações relacionadas a receitas médicas."
)
public class PrescriptionController {

    @Autowired
    private PrescricaoService prescricaoService;

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private ProfissionalService profissionalService;

    @Autowired
    private TokenService tokenService;

    @PreAuthorize("hasAuthority('PROFESSIONAL_UPLOAD_PRESCRIPTION')")
    @PostMapping(value = "/{pacienteNomeUsuario}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Cadastra receita médica de paciente",
            description = "Cadastra receita médica de paciente."
    )
    public ResponseEntity<Void> salvarPrescricaoDePaciente(@PathVariable("pacienteNomeUsuario") String pacienteNomeUsuario,
                                                           @RequestParam String issueDate,
                                                           @RequestParam String expirationDate,
                                                           @RequestParam String medicines,
                                                           @RequestParam boolean controlled,
                                                           @RequestPart("arquivo") MultipartFile arquivo) throws IOException {
        Optional<Profissional> optionalProfissional = (Optional<Profissional>) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(optionalProfissional.isPresent()) {
            pacienteService.salvarPrescricaoDePaciente(pacienteNomeUsuario, optionalProfissional.get(), issueDate, expirationDate, medicines, controlled, arquivo);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(401).build();
    }

    @PreAuthorize("hasAuthority('PATIENT_DOWNLOAD_PRESCRIPTION')")
    @PostMapping(value = "/{profissionalNomeUsuario}/{number}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(
            summary = "Realiza download de receita médica",
            description = "Realiza download de receita médica pelo paciente."
    )
    public ResponseEntity<byte[]> baixarPdf(@PathVariable("profissionalNomeUsuario") String profissionalNomeUsuario, @PathVariable String number) {
        Optional<Paciente> optionalPaciente = (Optional<Paciente>) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (optionalPaciente.isPresent()) {
            PrescriptionDTO receita = prescricaoService.retornarPrescricaoPorNumber(optionalPaciente.get().getNomeUsuario(), profissionalNomeUsuario, number);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).header(HttpHeaders.CONTENT_DISPOSITION,
                    "inline; filename=\"" + receita.getPrescriptionDocument().getNomeArquivo() + "\"").body(receita.getPrescriptionDocument().getArquivoPdf());
        }
        return ResponseEntity.status(401).build();
    }
}