package com.fiap.mindcare_diary.controllers;

import com.fiap.mindcare_diary.models.dtos.MiaMessageRequest;
import com.fiap.mindcare_diary.models.dtos.MiaMessageResponse;
import com.fiap.mindcare_diary.services.mia.MiaService;
import com.fiap.mindcare_diary.services.mia.MiaRegistroService;
import com.fiap.mindcare_diary.models.dtos.MiaRegistroRequest;
import com.fiap.mindcare_diary.models.dtos.RegistroDiarioDTO;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mia")
public class MiaController {
    private final MiaService service;
    private final MiaRegistroService records;

    public MiaController(MiaService service, MiaRegistroService records) {
        this.service = service;
        this.records = records;
    }

    @PostMapping("/registros")
    public ResponseEntity<RegistroDiarioDTO> save(Authentication authentication, @RequestBody MiaRegistroRequest request) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(records.save(authentication, request));
    }

    @PostMapping("/mensagens")
    public ResponseEntity<MiaMessageResponse> reply(Authentication authentication,
                                                   @RequestBody MiaMessageRequest request) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(service.reply(authentication, request));
    }
}
