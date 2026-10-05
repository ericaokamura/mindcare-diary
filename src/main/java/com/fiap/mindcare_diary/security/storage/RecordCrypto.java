package com.fiap.mindcare_diary.security.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/** Versioned AES-256-GCM envelopes. Keys are supplied by the operator, never generated at startup. */
@Component
public final class RecordCrypto {
    private static final String PREFIX = "mc1:";
    private final SecretKeySpec key;
    private final SecureRandom random = new SecureRandom();

    public RecordCrypto(@Value("${mindcare.crypto.key:}") String encodedKey) {
        byte[] bytes;
        try { bytes = Base64.getDecoder().decode(encodedKey); }
        catch (RuntimeException ex) { throw new IllegalStateException("MINDCARE_DATA_KEY deve conter 32 bytes em Base64."); }
        if (bytes.length != 32) throw new IllegalStateException("Configure MINDCARE_DATA_KEY com uma chave de 32 bytes em Base64.");
        key = new SecretKeySpec(bytes, "AES");
        Arrays.fill(bytes, (byte) 0);
    }

    public String encrypt(String value, String field) {
        if (value == null) return null;
        try {
            byte[] iv = new byte[12]; random.nextBytes(iv);
            Cipher cipher = cipher(Cipher.ENCRYPT_MODE, iv, field);
            byte[] encrypted = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
            byte[] envelope = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, envelope, 0, iv.length);
            System.arraycopy(encrypted, 0, envelope, iv.length, encrypted.length);
            return PREFIX + Base64.getEncoder().encodeToString(envelope);
        } catch (Exception ex) { throw new IllegalStateException("Não foi possível proteger o registro."); }
    }

    public String decrypt(String value, String field) {
        if (value == null) return null;
        try {
            if (!value.startsWith(PREFIX)) throw new IllegalArgumentException();
            byte[] envelope = Base64.getDecoder().decode(value.substring(PREFIX.length()));
            if (envelope.length < 28) throw new IllegalArgumentException();
            Cipher cipher = cipher(Cipher.DECRYPT_MODE, Arrays.copyOf(envelope, 12), field);
            return new String(cipher.doFinal(envelope, 12, envelope.length - 12), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            // Never return ciphertext/plaintext on failure, nor include the stored value or key in logs.
            throw new IllegalStateException("Registro protegido inválido ou chave de dados incorreta.");
        }
    }

    private Cipher cipher(int mode, byte[] iv, String field) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(mode, key, new GCMParameterSpec(128, iv));
        cipher.updateAAD((PREFIX + field).getBytes(StandardCharsets.UTF_8));
        return cipher;
    }
}
