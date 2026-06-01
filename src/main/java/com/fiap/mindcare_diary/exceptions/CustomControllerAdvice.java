package com.fiap.mindcare_diary.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomControllerAdvice {

    @ExceptionHandler(value = { AgendamentoNaoPodeSerRealizadoException.class })
    public ResponseEntity<ErrorDTO> handleAgendamentoNaoPodeSerRealizadoException(AgendamentoNaoPodeSerRealizadoException exception) {
        return ResponseEntity.badRequest().body(new ErrorDTO(400, exception.getMessage()));
    }

    @ExceptionHandler(value = { PacienteNaoEncontradoException.class })
    public ResponseEntity<ErrorDTO> handlePacienteNaoEncontradoException(PacienteNaoEncontradoException exception) {
        return ResponseEntity.badRequest().body(new ErrorDTO(400, exception.getMessage()));
    }

    @ExceptionHandler(value = { ProfissionalJaCadastradoException.class })
    public ResponseEntity<ErrorDTO> handleProfissionalJaCadastradoException(ProfissionalJaCadastradoException exception) {
        return ResponseEntity.badRequest().body(new ErrorDTO(400, exception.getMessage()));
    }

    @ExceptionHandler(value = { ProfissionalNaoEncontradoException.class })
    public ResponseEntity<ErrorDTO> handleProfissionalNaoEncontradoException(ProfissionalNaoEncontradoException exception) {
        return ResponseEntity.badRequest().body(new ErrorDTO(400, exception.getMessage()));
    }
}
