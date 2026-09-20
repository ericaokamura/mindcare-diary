package com.fiap.mindcare_diary.models.dtos;

public record MiaMessageResponse(String assistente, String papel, String mensagem, boolean fallback) {
}
