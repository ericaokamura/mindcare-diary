package com.fiap.mindcare_diary.exceptions;

public class RelatorioSemanalNaoExistenteException extends RuntimeException{

    public RelatorioSemanalNaoExistenteException(String mensagem) {
        super(mensagem);
    }
}
