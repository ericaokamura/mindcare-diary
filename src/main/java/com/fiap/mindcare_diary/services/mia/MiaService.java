package com.fiap.mindcare_diary.services.mia;

import com.fiap.mindcare_diary.models.Usuario;
import com.fiap.mindcare_diary.models.dtos.MiaMessageRequest;
import com.fiap.mindcare_diary.models.dtos.MiaMessageResponse;
import com.fiap.mindcare_diary.models.enums.UserRole;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.concurrent.Semaphore;

@Service
public class MiaService {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MiaService.class);
    public static final int MAX_MESSAGE_LENGTH = 4000;
    private final MiaIntentClassifier classifier;
    private final MiaAiService ai;
    private final MiaResponseValidator validator;
    private final Semaphore aiSlots = new Semaphore(4);

    public MiaService(MiaIntentClassifier classifier, MiaAiService ai, MiaResponseValidator validator) {
        this.classifier = classifier;
        this.ai = ai;
        this.validator = validator;
    }

    public MiaMessageResponse reply(Authentication authentication, MiaMessageRequest request) {
        MiaAuthentication.requirePatient(authentication);
        if (request == null || request.mensagem() == null || request.mensagem().isBlank()
                || request.mensagem().length() > MAX_MESSAGE_LENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe uma mensagem de 1 a 4000 caracteres.");
        }
        String message = request.mensagem().strip();
        var intent = classifier.classify(message);
        if (intent.isPresent()) return response(MiaSafeResponses.forIntent(intent.get()), false);
        if (!aiSlots.tryAcquire()) {
            LOG.warn("MIA fallback: CAPACIDADE_OCUPADA");
            return response(MiaSafeResponses.unavailable(), true);
        }
        try {
            return validator.validate(ai.generate(message))
                    .map(text -> response(text, false))
                    .orElseGet(() -> {
                        LOG.warn("MIA fallback: RESPOSTA_INVALIDA");
                        return response(MiaSafeResponses.unavailable(), true);
                    });
        } catch (RuntimeException failure) {
            // Do not log exception messages, bodies, stack traces or patient content.
            LOG.warn("MIA fallback: FALHA_PROVEDOR tipo={}", failure.getClass().getSimpleName());
            return response(MiaSafeResponses.unavailable(), true);
        } finally {
            aiSlots.release();
        }
    }

    private MiaMessageResponse response(String text, boolean fallback) {
        return new MiaMessageResponse("MIA", "Assistente de registro", text, fallback);
    }
}
