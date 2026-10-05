package com.fiap.mindcare_diary.exceptions;

import com.fiap.mindcare_diary.controllers.MiaController;
import com.fiap.mindcare_diary.controllers.RegistroDiarioController;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@org.springframework.core.annotation.Order(org.springframework.core.Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = {MiaController.class, RegistroDiarioController.class,
        com.fiap.mindcare_diary.controllers.HistoricoController.class,
        com.fiap.mindcare_diary.controllers.PrivacidadeController.class})
public class MiaExceptionHandler {

    @ExceptionHandler(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorDTO> invalidFilter() {
        return ResponseEntity.badRequest().cacheControl(CacheControl.noStore())
                .body(new ErrorDTO(400, "Confira os filtros informados."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> unexpectedError() {
        return ResponseEntity.internalServerError().cacheControl(CacheControl.noStore())
                .body(new ErrorDTO(500, "Não foi possível concluir a operação."));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDTO> malformedBody() {
        return ResponseEntity.badRequest().cacheControl(CacheControl.noStore())
                .body(new ErrorDTO(400, "Corpo JSON inválido."));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorDTO> rejectedRequest(ResponseStatusException exception) {
        return ResponseEntity.status(exception.getStatusCode()).cacheControl(CacheControl.noStore())
                .body(new ErrorDTO(exception.getStatusCode().value(), exception.getReason()));
    }
}
