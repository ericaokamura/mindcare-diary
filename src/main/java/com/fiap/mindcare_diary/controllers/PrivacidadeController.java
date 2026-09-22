package com.fiap.mindcare_diary.controllers;

import com.fiap.mindcare_diary.services.PrivacidadeService;
import com.fiap.mindcare_diary.services.mia.MiaAuthentication;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/minha-conta")
public class PrivacidadeController {
    private final PrivacidadeService service;
    public PrivacidadeController(PrivacidadeService service) { this.service = service; }
    public record Confirmacao(String senha) {
        @Override public String toString() { return "Confirmacao[redigido]"; }
    }
    @PostMapping(value = "/exportacao", produces = "application/zip")
    public ResponseEntity<byte[]> exportar(Authentication auth, @RequestBody Confirmacao request) {
        var user = MiaAuthentication.requireActiveUser(auth);
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=meus-dados-mindcare.zip")
                .body(service.exportar(user.getId(), request.senha()));
    }
    @PostMapping("/encerramento")
    public ResponseEntity<Map<String, Object>> encerrar(Authentication auth, @RequestBody Confirmacao request) {
        var user = MiaAuthentication.requireActiveUser(auth);
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(service.encerrar(user.getId(), request.senha()));
    }
}
