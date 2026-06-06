package com.gym.crm.exception;

import org.springframework.security.core.AuthenticationException;

public class BadCredentialsException extends AuthenticationException {
    public BadCredentialsException(String message) {
        super(message);
    }
}
