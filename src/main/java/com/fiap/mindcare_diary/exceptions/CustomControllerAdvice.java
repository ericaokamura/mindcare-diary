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

    @ExceptionHandler(value = { ClinicaNaoExistenteException.class })
    public ResponseEntity<ErrorDTO> handleClinicaNaoExistenteException(ClinicaNaoExistenteException exception) {
        return ResponseEntity.badRequest().body(new ErrorDTO(400, exception.getMessage()));
    }

    @ExceptionHandler(value = { ConsultaNaoEncontradaException.class })
    public ResponseEntity<ErrorDTO> handleConsultaNaoEncontradaException(ConsultaNaoEncontradaException exception) {
        return ResponseEntity.badRequest().body(new ErrorDTO(400, exception.getMessage()));
    }

    @ExceptionHandler(value = { PrescricaoNaoEncontradaException.class })
    public ResponseEntity<ErrorDTO> handlePrescricaoNaoEncontradaException(PrescricaoNaoEncontradaException exception) {
        return ResponseEntity.badRequest().body(new ErrorDTO(400, exception.getMessage()));
    }

    @ExceptionHandler(value = { RelatorioSemanalNaoExistenteException.class })
    public ResponseEntity<ErrorDTO> handleRelatorioSemanalNaoExistenteException(RelatorioSemanalNaoExistenteException exception) {
        return ResponseEntity.badRequest().body(new ErrorDTO(400, exception.getMessage()));
    }

    @ExceptionHandler(value = { UsuarioNaoEncontradoException.class })
    public ResponseEntity<ErrorDTO> handleUsuarioNaoEncontradoException(UsuarioNaoEncontradoException exception) {
        return ResponseEntity.badRequest().body(new ErrorDTO(400, exception.getMessage()));
    }

    @ExceptionHandler(value = { PacienteNaoEncontradoParaEsteProfissionalException.class })
    public ResponseEntity<ErrorDTO> handlePacienteNaoEncontradoParaEsteProfissionalException(PacienteNaoEncontradoParaEsteProfissionalException exception) {
        return ResponseEntity.badRequest().body(new ErrorDTO(400, exception.getMessage()));
    }

    @ExceptionHandler(value = { UsuarioBloqueadoException.class })
    public ResponseEntity<ErrorDTO> handleUsuarioBloqueadoException(UsuarioBloqueadoException exception) {
        return ResponseEntity.badRequest().body(new ErrorDTO(400, exception.getMessage()));
    }

    @ExceptionHandler(value = { UsuarioJaExistenteException.class })
    public ResponseEntity<ErrorDTO> handleUsuarioJaExistenteException(UsuarioJaExistenteException exception) {
        return ResponseEntity.badRequest().body(new ErrorDTO(400, exception.getMessage()));
    }

}
