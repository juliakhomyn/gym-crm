package com.gym.crm.service;

import com.gym.crm.auth.SessionContext;
import com.gym.crm.dao.UserDAO;
import com.gym.crm.dto.common.AuthRequestDTO;
import com.gym.crm.dto.common.AuthResponseDTO;
import com.gym.crm.exception.BadCredentialsException;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.model.User;
import com.gym.crm.service.common.UserInputValidator;
import com.gym.crm.service.common.UserProfileService;
import com.gym.crm.service.impl.AuthenticationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceImplTest {
    private static final String USERNAME = "Cillian.Mercer";
    private static final String PASSWORD = "password";
    private static final String INVALID_PASSWORD = "invalid";

    private static final String AUTH_SUCCESS_MESSAGE = "Authentication successful!";

    @Mock
    private UserDAO dao;
    @Mock
    private UserProfileService userProfileService;
    @Mock
    private UserInputValidator validator;
    @Mock
    private SessionContext sessionContext;

    @InjectMocks
    private AuthenticationServiceImpl service;

    private User user;
    private AuthRequestDTO request;
    private AuthRequestDTO requestInvalidPassword;

    @BeforeEach
    void setUp() {
        user = buildUser();
        request = buildAuthRequestDTO();
        requestInvalidPassword = buildAuthRequestDTOWithInvalidPassword();
    }

    @Test
    void authenticate_shouldReturnResponse_whenCredentialsAreValid() {
        when(dao.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(userProfileService.checkPassword(PASSWORD, PASSWORD)).thenReturn(true);

        AuthResponseDTO actual = service.authenticate(request);

        assertThat(actual.getUsername()).isEqualTo(USERNAME);
        assertThat(actual.getMessage()).isEqualTo(AUTH_SUCCESS_MESSAGE);
        verify(sessionContext).setAuthenticatedUser(user);
        verify(validator).validate(request, "Authentication request");
    }

    @Test
    void authenticate_shouldThrowEntityNotFoundException_whenUserNotFound() {
        when(dao.findByUsername(USERNAME)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.authenticate(request));

        assertThat(exception.getMessage()).contains("User not found");
        verify(validator).validate(request, "Authentication request");
        verify(sessionContext, never()).setAuthenticatedUser(any());
    }

    @Test
    void authenticate_shouldThrowBadCredentialsException_whenPasswordInvalid() {
        when(dao.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(userProfileService.checkPassword(INVALID_PASSWORD, PASSWORD)).thenReturn(false);

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> service.authenticate(requestInvalidPassword));

        assertThat(exception.getMessage()).contains("Invalid credentials");
        verify(validator).validate(requestInvalidPassword, "Authentication request");
        verify(sessionContext, never()).setAuthenticatedUser(any());
    }

    @Test
    void logout_shouldCallClearOnSessionContext() {
        when(sessionContext.getAuthenticatedUser()).thenReturn(user);

        service.logout();

        verify(sessionContext).clear();
    }

    @Test
    void logout_shouldClearAuthenticatedUser() {
        SessionContext realSessionContext = new SessionContext();
        AuthenticationServiceImpl realService = new AuthenticationServiceImpl(dao, userProfileService, validator, realSessionContext);
        realSessionContext.setAuthenticatedUser(user);

        realService.logout();

        assertThat(realSessionContext.getAuthenticatedUser()).isNull();
    }

    private AuthRequestDTO buildAuthRequestDTO() {
        return AuthRequestDTO.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .build();
    }

    private AuthRequestDTO buildAuthRequestDTOWithInvalidPassword() {
        return AuthRequestDTO.builder()
                .username(USERNAME)
                .password(INVALID_PASSWORD)
                .build();
    }

    private User buildUser() {
        return User.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .build();
    }
}
