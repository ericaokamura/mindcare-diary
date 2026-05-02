package com.fiap.mindcare_diary.models.enums;

public enum NivelHumor {

    OTIMO(0, "ÓTIMO"),
    BOM(1, "BOM"),
    NEUTRO(2, "NEUTRO"),
    MAL(3, "MAL"),
    PESSIMO(4, "PÉSSIMO");

    private int codigo;
    private String descricao;

    NivelHumor(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }
}
