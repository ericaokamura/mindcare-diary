package com.fiap.mindcare_diary.exceptions;

public class UsuarioBloqueadoException extends RuntimeException{

    public UsuarioBloqueadoException(String mensagem) {
        super(mensagem);
    }
}
