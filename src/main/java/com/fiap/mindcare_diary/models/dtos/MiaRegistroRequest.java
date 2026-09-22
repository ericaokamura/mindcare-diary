package com.fiap.mindcare_diary.models.dtos;

import java.util.UUID;

public record MiaRegistroRequest(UUID idRequisicao, String textoConfirmado, String nivelHumor) {
    @Override public String toString() { return "MiaRegistroRequest[conteudo omitido]"; }
}
