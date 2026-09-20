package com.fiap.mindcare_diary.controllers;

import com.fiap.mindcare_diary.exceptions.ErrorDTO;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice(assignableTypes = {MiaController.class, RegistroDiarioController.class})
public class MiaControllerAdvice {
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDTO> malformedBody() {
        // Do not let the default resolver log JSON parser excerpts from the patient's body.
        return ResponseEntity.badRequest().cacheControl(CacheControl.noStore())
                .body(new ErrorDTO(400, "Corpo JSON inválido."));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorDTO> rejectedRequest(ResponseStatusException exception) {
        return ResponseEntity.status(exception.getStatusCode()).cacheControl(CacheControl.noStore())
                .body(new ErrorDTO(exception.getStatusCode().value(), exception.getReason()));
    }
}
