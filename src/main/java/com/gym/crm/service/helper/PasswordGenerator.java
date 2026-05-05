package com.gym.crm.service.helper;

import org.springframework.stereotype.Component;

@Component
public class PasswordGenerator {
    private static final int PASSWORD_LENGTH = 10;
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            + "abcdefghijklmnopqrstuvxyz"
            + "0123456789";

    public String generatePassword() {
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = (int) (Math.random() * CHARS.length());
            password.append(CHARS.charAt(index));
        }

        return password.toString();
    }
}
