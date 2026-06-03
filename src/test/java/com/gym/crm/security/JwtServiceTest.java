package com.gym.crm.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {
    private static final String USERNAME = "Simone.Radcliffe";
    private static final String JWT_SECRET = "QWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXo1Njc4OTAxMjM0NTY3OA==";
    private static final long JWT_EXPIRATION_MS = 3600000;
    
    private final JwtService jwtService = new JwtService();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "jwtSecret", JWT_SECRET);
        ReflectionTestUtils.setField(jwtService, "jwnExpirationMs", JWT_EXPIRATION_MS);
    }

    @Test
    void generateToken_shouldCreateValidToken() {
        String token = jwtService.generateToken(USERNAME);

        assertThat(token).isNotNull();
    }

    @Test
    void extractUsername_shouldReturnCorrectUsername() {
        String token = jwtService.generateToken(USERNAME);

        String expected = jwtService.extractUsername(token);

        assertThat(expected).isEqualTo(USERNAME);
    }

    @Test
    void isTokenValid_shouldReturnTrueForValidToken() {
        String token = jwtService.generateToken(USERNAME);

        assertThat(jwtService.isTokenValid(token)).isTrue();
    }

    @Test
    void isTokenValid_shouldReturnFalseForInvalidToken() {
        String invalidToken = "invalid.token.value";

        assertThat(jwtService.isTokenValid(invalidToken)).isFalse();
    }
}
