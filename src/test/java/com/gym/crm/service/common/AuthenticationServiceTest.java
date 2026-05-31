package com.gym.crm.service.common;

import com.gym.crm.auth.SessionContext;
import com.gym.crm.dto.common.AuthRequestDTO;
import com.gym.crm.dto.common.AuthResponseDTO;
import com.gym.crm.exception.BadCredentialsException;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.model.User;
import com.gym.crm.repository.UserRepository;
import com.gym.crm.utils.TestDataProvider;
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
class AuthenticationServiceTest {
    private static final String USERNAME = "Simone.Radcliffe";
    private static final String PASSWORD = "password";
    private static final String ENCODED_PASSWORD = "encodedPassword";
    private static final String INVALID_PASSWORD = "invalidPassword";

    private static final String AUTH_SUCCESS_MESSAGE = "Authentication successful!";

    private final User user = TestDataProvider.buildTraineeUser();
    private final AuthRequestDTO request = TestDataProvider.buildAuthRequestDTO();
    private final AuthRequestDTO requestInvalidPassword = TestDataProvider.buildAuthRequestDTOWithInvalidPassword();

    @Mock
    private UserProfileService userProfileService;
    @Mock
    private SessionContext sessionContext;
    @Mock
    private UserRepository repository;

    @InjectMocks
    private AuthenticationService service;

    @Test
    void authenticate_shouldReturnResponse_whenCredentialsAreValid() {
        when(repository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(userProfileService.checkPassword(PASSWORD, ENCODED_PASSWORD)).thenReturn(true);

        AuthResponseDTO actual = service.authenticate(request);

        assertThat(actual.getUsername()).isEqualTo(USERNAME);
        assertThat(actual.getMessage()).isEqualTo(AUTH_SUCCESS_MESSAGE);
        verify(sessionContext).setAuthenticatedUser(user);
    }

    @Test
    void authenticate_shouldThrowEntityNotFoundException_whenUserNotFound() {
        when(repository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.authenticate(request));

        assertThat(exception.getMessage()).contains("User not found");
        verify(sessionContext, never()).setAuthenticatedUser(any());
    }

    @Test
    void authenticate_shouldThrowBadCredentialsException_whenPasswordInvalid() {
        when(repository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(userProfileService.checkPassword(INVALID_PASSWORD, ENCODED_PASSWORD)).thenReturn(false);

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> service.authenticate(requestInvalidPassword));

        assertThat(exception.getMessage()).contains("Invalid credentials");
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
        AuthenticationService realService = new AuthenticationService(repository, userProfileService, realSessionContext);
        realSessionContext.setAuthenticatedUser(user);

        realService.logout();

        assertThat(realSessionContext.getAuthenticatedUser()).isNull();
    }
}
