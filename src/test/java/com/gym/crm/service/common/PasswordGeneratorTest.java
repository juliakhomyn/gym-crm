package com.gym.crm.service.common;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordGeneratorTest {
    private static final int PASSWORD_LENGTH = 10;
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            + "abcdefghijklmnopqrstuvxyz"
            + "0123456789";

    private final PasswordGenerator passwordGenerator = new PasswordGenerator();

    @Test
    void generatePassword_shouldReturnPasswordOfCorrectLength() {
        String actual = passwordGenerator.generatePassword();

        assertThat(actual).hasSize(PASSWORD_LENGTH);
    }

    @Test
    void generatePassword_shouldReturnPasswordWithAllowedCharacters() {
        String actual = passwordGenerator.generatePassword();

        assertThat(actual.chars()).allMatch(c -> CHARS.indexOf(c) >= 0);
    }

    @Test
    public void generatePassword_shouldReturnDifferentPasswords_whenCalledTwoTimes() {
        String password1 = passwordGenerator.generatePassword();
        String password2 = passwordGenerator.generatePassword();

        assertThat(password2).isNotEqualTo(password1);
    }
}
