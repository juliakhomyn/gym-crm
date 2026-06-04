package com.gym.crm.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TokenBlacklistServiceTest {

    private static final String TOKEN = "token";

    private TokenBlacklistService tokenBlacklistService;

    @BeforeEach
    void setUp() {
        tokenBlacklistService = new TokenBlacklistService();
    }

    @Test
    void isBlacklisted_shouldReturnFalse_whenTokenNotBlacklisted() {
        boolean actual = tokenBlacklistService.isBlacklisted(TOKEN);

        assertThat(actual).isFalse();
    }

    @Test
    void blacklist_shouldAddTokenToBlacklist() {
        tokenBlacklistService.blacklist(TOKEN);

        boolean actual = tokenBlacklistService.isBlacklisted(TOKEN);

        assertThat(actual).isTrue();
    }

    @Test
    void isBlacklisted_shouldReturnTrueOnlyForBlacklistedTokens() {
        tokenBlacklistService.blacklist(TOKEN);

        boolean actualBlacklisted = tokenBlacklistService.isBlacklisted(TOKEN);
        boolean actualNotBlacklisted = tokenBlacklistService.isBlacklisted("notBlacklistedToken");

        assertThat(actualBlacklisted).isTrue();
        assertThat(actualNotBlacklisted).isFalse();
    }
}
