package com.fiap.mindcare_diary.models.enums;

public enum Sexo {

    FEMININO(0, "FEMININO"), MASCULINO(1, "MASCULINO");

    public int codigo;
    public String descricao;

    Sexo(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }
}
