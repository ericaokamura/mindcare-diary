package com.fiap.mindcare_diary.security.storage;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/** Hibernate obtains these converters through Spring's BeanContainer (constructor injection). */
public final class EncryptedConverters {
    private EncryptedConverters() {}
    public abstract static class Text implements AttributeConverter<String, String> {
        private final RecordCrypto crypto;
        private final String field;
        protected Text(RecordCrypto crypto, String field) { this.crypto = crypto; this.field = field; }
        public String convertToDatabaseColumn(String value) { return crypto.encrypt(value, field); }
        public String convertToEntityAttribute(String value) { return crypto.decrypt(value, field); }
    }
    @Converter
    public static class Pontos extends Text {
        public Pontos(RecordCrypto crypto) { super(crypto, "registro_diario.pontos_positivos"); }
    }
    @Converter
    public static class Dificuldades extends Text {
        public Dificuldades(RecordCrypto crypto) { super(crypto, "registro_diario.dificuldades_desafios"); }
    }
    @Converter
    public static class Texto extends Text {
        public Texto(RecordCrypto crypto) { super(crypto, "registro_diario.texto_confirmado"); }
    }
    @Converter
    public static class Observacoes extends Text {
        public Observacoes(RecordCrypto crypto) { super(crypto, "relatorio_semanal.observacoes"); }
    }
    @Converter
    public static class Recomendacoes extends Text {
        public Recomendacoes(RecordCrypto crypto) { super(crypto, "relatorio_semanal.recomendacoes"); }
    }
    @Converter
    public static class Relatorio extends Text {
        public Relatorio(RecordCrypto crypto) { super(crypto, "relatorio_semanal.relatorio_ia"); }
    }
    @Converter
    public static class Resumo extends Text {
        public Resumo(RecordCrypto crypto) { super(crypto, "relatorio_semanal.resumo"); }
    }
}
