package com.fiap.mindcare_diary.models.enums;

public enum ConsultaModalidade {

    PRESENCIAL(0, "PRESENCIAL"), TELECONSULTA(1, "TELECONSULTA");

    private int codigo;
    private String descricao;

    ConsultaModalidade(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

}
