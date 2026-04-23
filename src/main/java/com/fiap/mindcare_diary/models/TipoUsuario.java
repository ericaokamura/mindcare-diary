package com.fiap.mindcare_diary.models;

public enum TipoUsuario {

    PACIENTE(0, "PACIENTE"),
    PSIQUIATRA(1, "PSIQUIATRA"),
    PSICOLOGO(2, "PSICOLOGO");

    private int codigo;
    private String descricao;

    TipoUsuario(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getCodigo() {
        return codigo;
    }
}