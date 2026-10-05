package com.fiap.mindcare_diary.controllers;

import com.fiap.mindcare_diary.services.HistoricoService;
import com.fiap.mindcare_diary.services.mia.MiaAuthentication;
import com.fiap.mindcare_diary.models.enums.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import java.time.LocalDate;

@RestController
@RequestMapping("/meu-diario/historico")
public class HistoricoController {
    private final HistoricoService service;
    public HistoricoController(HistoricoService service) { this.service = service; }
    @GetMapping
    public ResponseEntity<HistoricoService.Pagina> buscar(Authentication auth,
            @RequestParam(required=false) String texto,
            @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(required=false) NivelHumor humor, @RequestParam(required=false) OrigemRegistro origem,
            @RequestParam(defaultValue="0") int pagina, @RequestParam(defaultValue="20") int tamanho) {
        var user = MiaAuthentication.requirePatient(auth);
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(service.buscar(user.getId(), texto, inicio, fim, humor, origem, pagina, tamanho));
    }
}
