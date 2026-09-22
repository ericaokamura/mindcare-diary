package com.fiap.mindcare_diary.services.mia;

import com.fiap.mindcare_diary.models.enums.MiaIntent;

public final class MiaSafeResponses {
    private MiaSafeResponses() {}

    public enum Question {
        DIA("Como foi o seu dia hoje?"),
        SENTIMENTO("Como você descreveria como está se sentindo agora?"),
        ACONTECIMENTO("Aconteceu algo hoje que você gostaria de registrar?"),
        DETALHES("Gostaria de contar um pouco mais sobre isso?"),
        IMPORTANTE("Tem algo sobre o seu dia que você considera importante registrar?"),
        ADICIONAR("Existe mais alguma coisa que você gostaria de adicionar ao seu registro de hoje?");

        private final String text;

        Question(String text) { this.text = text; }

        public String text() { return text; }
    }

    public static String forIntent(MiaIntent intent) {
        return switch (intent) {
            case REGISTRO_NORMAL -> Question.DETALHES.text();
            case PEDIDO_CONSELHO -> "Sou a MIA, sua assistente de registro. Minha função é ajudar você a organizar e registrar o que deseja contar, mas não posso oferecer conselhos ou orientações clínicas. Para esse tipo de dúvida, converse com um profissional qualificado.";
            case PEDIDO_DIAGNOSTICO -> "Não posso realizar diagnósticos ou avaliações clínicas. Posso ajudar você a registrar o que está sentindo para que essas informações possam ser discutidas com um profissional.";
            case MEDICAMENTO -> "Não posso recomendar medicamentos ou tratamentos. Esse tipo de orientação deve ser realizada por um profissional habilitado. Se quiser, posso ajudar você a registrar essa dúvida para conversar com ele.";
            case PEDIDO_OPINIAO -> "Não cabe a mim julgar ou tomar uma posição sobre essa situação. Posso ajudar você a registrar o que aconteceu e como você se sentiu.";
            case FORA_DO_ESCOPO -> "Minha função é ajudar você a registrar seu dia e suas experiências. Para dúvidas que exigem orientação profissional, converse com um profissional qualificado.";
            case SITUACAO_SENSIVEL -> "Sua segurança é importante. Sou uma assistente de registro e não ofereço atendimento de emergência. Se houver risco imediato de você ou outra pessoa se machucar, procure um serviço de emergência local ou uma pessoa de confiança que possa estar com você. Este chat não aciona ajuda automaticamente.";
        };
    }

    public static String unavailable() {
        return "Não consegui processar sua mensagem agora. Sou a MIA, assistente de registro. Você pode tentar a resposta novamente ou revisar seu relato para salvar no diário.";
    }
}
