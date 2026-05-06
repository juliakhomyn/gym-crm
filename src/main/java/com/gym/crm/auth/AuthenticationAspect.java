package com.gym.crm.auth;

import com.gym.crm.model.User;
import com.gym.crm.exception.UserAuthenticationException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Aspect
@Component
@RequiredArgsConstructor
public class AuthenticationAspect {

    private final SessionContext sessionContext;

    @Before(value = "@annotation(com.gym.crm.auth.Authenticated) && args(callerUsername, ..)", argNames = "callerUsername")
    public void checkAuthentication(String callerUsername) {
        User user = sessionContext.getAuthenticatedUser();

        Optional.ofNullable(user)
                .orElseThrow(() -> new UserAuthenticationException("No user authenticated"));

        Optional.ofNullable(callerUsername)
                .orElseThrow(() -> new UserAuthenticationException("User is not authenticated: no request to check authentication"));

        if (!user.getUsername().equals(callerUsername)) {
            throw new UserAuthenticationException(String.format("Authenticated user with username: %s does not match with requested user with username: %s", user.getUsername(), callerUsername));
        }
    }
}
