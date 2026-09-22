package com.fiap.mindcare_diary.services.mia;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.mindcare_diary.models.enums.MiaIntent;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MiaResponseValidator {
    private final ObjectMapper mapper;

    public MiaResponseValidator(ObjectMapper mapper) {
        this.mapper = mapper.copy()
                .enable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION)
                .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS);
    }

    public Optional<String> validate(String generated) {
        if (generated == null || generated.length() > 512) return Optional.empty();
        try {
            JsonNode json = mapper.readTree(generated);
            if (json == null || !json.isObject() || json.size() != 2
                    || !json.path("intencao").isTextual() || !json.path("pergunta").isTextual()) {
                return Optional.empty();
            }
            MiaIntent intent = MiaIntent.valueOf(json.get("intencao").textValue());
            MiaSafeResponses.Question question = MiaSafeResponses.Question.valueOf(json.get("pergunta").textValue());
            // Never return model prose. Both paths resolve to reviewed server-owned text.
            return Optional.of(intent == MiaIntent.REGISTRO_NORMAL
                    ? question.text() : MiaSafeResponses.forIntent(intent));
        } catch (Exception ignored) {
            // Provider output can contain sensitive data; never log it or parser exceptions.
            return Optional.empty();
        }
    }
}
