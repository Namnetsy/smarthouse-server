package com.gmail.roma.teodorovich.server.helper;

import java.security.SecureRandom;

public class SecretHelper {

    public static String generateSecret(int length) {
        char[] allowedChars = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890".toCharArray();
        SecureRandom random = new SecureRandom();
        StringBuilder secretBuilder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            secretBuilder.append(allowedChars[random.nextInt(allowedChars.length)]);
        }

        return secretBuilder.toString();
    }

}
