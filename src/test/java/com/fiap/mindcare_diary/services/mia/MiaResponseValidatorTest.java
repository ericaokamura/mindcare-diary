package com.fiap.mindcare_diary.services.mia;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.mindcare_diary.models.enums.MiaIntent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class MiaResponseValidatorTest {

    private final MiaResponseValidator validator = new MiaResponseValidator(new ObjectMapper());

    @ParameterizedTest
    @EnumSource(MiaSafeResponses.Question.class)
    void acceptsOnlyCatalogQuestions(MiaSafeResponses.Question question) {
        assertEquals(question.text(), validator.validate("{\"intencao\":\"REGISTRO_NORMAL\",\"pergunta\":\""
                + question.name() + "\"}").orElseThrow());
    }

    @ParameterizedTest
    @EnumSource(MiaIntent.class)
    void mapsEveryIntentToServerText(MiaIntent intent) {
        assertEquals(MiaSafeResponses.forIntent(intent), validator.validate("{\"intencao\":\""
                + intent.name() + "\",\"pergunta\":\"DETALHES\"}").orElseThrow());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            "Você tem depressão", "Tome sertralina", "Você deve terminar seu relacionamento",
            "Eu entendo exatamente como você se sente", "Sou sua psicóloga",
            "null", "[]", "{}", "{\"intencao\":\"REGISTRO_NORMAL\"}",
            "{\"intencao\":\"REGISTRO_NORMAL\",\"pergunta\":\"DIA\",\"texto\":\"Tome remédio\"}",
            "{\"intencao\":\"REGISTRO_NORMAL\",\"pergunta\":\"Como você trata sua depressão?\"}",
            "{\"intencao\":\"DESCONHECIDA\",\"pergunta\":\"DIA\"}",
            "{\"intencao\":\"REGISTRO_NORMAL\",\"pergunta\":null}",
            "{\"intencao\":\"REGISTRO_NORMAL\",\"pergunta\":\"DIA\"} {}",
            "{\"intencao\":\"REGISTRO_NORMAL\",\"pergunta\":\"DIA\",\"pergunta\":\"DETALHES\"}",
            "```json\n{\"intencao\":\"REGISTRO_NORMAL\",\"pergunta\":\"DIA\"}\n```"
    })
    void rejectsInvalidOrUnreviewedOutput(String output) {
        assertTrue(validator.validate(output).isEmpty());
    }

    @Test
    void rejectsOversizedOutput() { assertTrue(validator.validate("x".repeat(513)).isEmpty()); }
}
