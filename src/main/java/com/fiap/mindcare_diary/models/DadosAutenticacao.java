package com.fiap.mindcare_diary.models;

public record DadosAutenticacao(String nomeUsuario, String senha, String versaoTermos) {
    public DadosAutenticacao(String nomeUsuario, String senha) { this(nomeUsuario, senha, null); }
    @Override public String toString() { return "DadosAutenticacao[redigido]"; }


}
