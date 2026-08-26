package com.fiap.mindcare_diary.models.enums;

public enum PlanoAssinatura {

    BASICO(0, "BASICO"), PROFISSIONAL(1, "PROFISSIONAL"), CLINICA(2, "CLINICA");

    public int codigo;
    public String descricao;

    PlanoAssinatura(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }
}
