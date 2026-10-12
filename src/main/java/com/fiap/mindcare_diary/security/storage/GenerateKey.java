package com.fiap.mindcare_diary.security.storage;

import java.security.SecureRandom;
import java.util.Base64;

public class GenerateKey {

    public static void main(String[] args) {
        byte[] key = new byte[32];
        new SecureRandom().nextBytes(key);
        String encoded = Base64.getEncoder().encodeToString(key);
        System.out.println(encoded);
    }
}
