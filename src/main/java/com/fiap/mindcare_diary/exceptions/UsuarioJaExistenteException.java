package com.fiap.mindcare_diary.exceptions;

public class UsuarioJaExistenteException extends RuntimeException {

    public UsuarioJaExistenteException(String message) {
        super(message);
    }
}
