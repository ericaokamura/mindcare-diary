package com.fiap.mindcare_diary.services.mia;

import com.fiap.mindcare_diary.models.enums.MiaIntent;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class MiaIntentClassifier {
    private static final Pattern SENSITIVE = pattern(
            "suicid|me matar|me machucar|me cortar|tirar (a |minha )?vida|nao (quero|aguento) mais viver|"
            + "quero morrer|overdose|automutil|autoagress|machucar alguem|matar alguem|"
            + "estou em perigo|sofrendo violencia|kill myself|kill someone|hurt myself|self.harm");
    private static final Pattern INJECTION = pattern(
            "ignore.{0,40}(instruc|regras|prompt)|system prompt|developer message|"
            + "finja (ser|que)|aja como|atue como|revele.{0,30}(prompt|segredo)|jailbreak");
    private static final Pattern MEDICATION = pattern(
            "\\b(remedio|remedios|medicamento|medicamentos|medicacao|dose|dosagem|"
            + "antidepressivo|ansiolitico|sertralina|fluoxetina|clonazepam|rivotril|tratamento|prescrever)\\b");
    private static final Pattern DIAGNOSIS = pattern(
            "diagnostic|\\btenho (depressao|ansiedade|bipolaridade|tdah|autismo)|"
            + "(sera que|voce acha que) (eu )?tenho|qual.{0,20}(doenca|transtorno)|avalie.{0,20}sintomas");
    private static final Pattern ADVICE = pattern(
            "conselho|me aconselh|o que (eu )?(devo|posso) fazer|como (eu )?devo|"
            + "devo (terminar|sair|ficar|aceitar|mudar)|me (diga|diz) o que fazer|me orient|what should i");
    private static final Pattern OPINION = pattern(
            "sua opiniao|o que (voce )?acha|quem (esta|ta) (certo|errado)|"
            + "(estou|sou) (certo|certa|errado|errada)|me julgue|voce concorda");
    private static final Pattern OUT_OF_SCOPE = pattern(
            "previsao do tempo|receita de bolo|codigo (java|python)|resolva.{0,20}equacao|"
            + "cotacao|criptomoeda|quem e o presidente");

    public Optional<MiaIntent> classify(String message) {
        String text = Normalizer.normalize(message, Normalizer.Form.NFKD)
                .replaceAll("\\p{M}+", "").replaceAll("\\p{Cf}", "")
                .toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
        // A sensitive situation takes precedence over every other intention.
        if (SENSITIVE.matcher(text).find()) return Optional.of(MiaIntent.SITUACAO_SENSIVEL);
        if (INJECTION.matcher(text).find()) return Optional.of(MiaIntent.FORA_DO_ESCOPO);
        if (MEDICATION.matcher(text).find()) return Optional.of(MiaIntent.MEDICAMENTO);
        if (DIAGNOSIS.matcher(text).find()) return Optional.of(MiaIntent.PEDIDO_DIAGNOSTICO);
        if (ADVICE.matcher(text).find()) return Optional.of(MiaIntent.PEDIDO_CONSELHO);
        if (OPINION.matcher(text).find()) return Optional.of(MiaIntent.PEDIDO_OPINIAO);
        if (OUT_OF_SCOPE.matcher(text).find()) return Optional.of(MiaIntent.FORA_DO_ESCOPO);
        // Unknown is not implicitly normal: the model must classify it next.
        return Optional.empty();
    }

    private static Pattern pattern(String regex) { return Pattern.compile(regex); }
}
