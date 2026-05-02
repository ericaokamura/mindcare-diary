package com.fiap.mindcare_diary.models.enums;

public enum EstadoPaciente {

    ESTAVEL(0, "ESTÁVEL"),
    ATENCAO(1, "ATENÇÃO"),
    MELHORANDO(2, "MELHORANDO");

    private int codigo;
    private String descricao;

    EstadoPaciente(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }
}
