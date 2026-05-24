package com.gym.crm.service;

import com.gym.crm.dao.UserDAO;
import com.gym.crm.dto.common.PasswordChangeRequest;
import com.gym.crm.dto.common.ToggleActiveRequestDTO;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.exception.ValidationFailedException;
import com.gym.crm.model.User;
import com.gym.crm.service.common.UserInputValidator;
import com.gym.crm.service.impl.UserServiceImpl;
import com.gym.crm.testutils.TestDataProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private static final String USERNAME = "Simone.Radcliffe";
    private static final String NON_EXISTENT_USERNAME = "Not.Found";
    private static final String NEW_PASSWORD = "newPassword";
    private static final long VALID_ID = 1L;
    private static final long NOT_FOUND_ID = 999L;
    private static final String ENCODED_NEW_PASSWORD = "encodedPassword";
    
    private static final String USER_NOT_FOUND_BY_USERNAME = "User not found by username: %s";
    private static final String USER_NOT_FOUND_BY_ID = "User not found by id: %s";
    private static final String USER_ALREADY_ACTIVATED = "Could not activate user %s: user is already activated";

    private final User user = TestDataProvider.buildTraineeUser();
    private final User savedUser = user.toBuilder().id(VALID_ID).build();
    private final PasswordChangeRequest request = TestDataProvider.buildPasswordChangeRequest();
    private final PasswordChangeRequest passwordChangeRequestBlankUsername = TestDataProvider.buildPasswordChangeRequestBlankUsername();
    private final PasswordChangeRequest passwordChangeRequestShortNewPassword = TestDataProvider.buildPasswordChangeRequestShortNewPassword();
    private final ToggleActiveRequestDTO toggleActiveRequestDTO = TestDataProvider.buildToggleActiveRequest();
    private final ToggleActiveRequestDTO invalidToggleActiveRequest = TestDataProvider.buildInvalidToggleActiveRequest();

    @Mock
    private UserDAO dao;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserInputValidator userInputValidator;

    @InjectMocks
    private UserServiceImpl service;

    @Test
    void getById_shouldReturnUser_whenUserExists() {
        when(dao.findById(VALID_ID)).thenReturn(Optional.of(savedUser));

        User actual = service.getById(VALID_ID);

        assertThat(actual).isEqualTo(savedUser);
    }

    @Test
    void getById_shouldThrowException_whenUserNotFound() {
        when(dao.findById(NOT_FOUND_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.getById(NOT_FOUND_ID));

        assertThat(exception.getMessage()).isEqualTo(String.format(USER_NOT_FOUND_BY_ID, NOT_FOUND_ID));
    }

    @Test
    void getByUsername_shouldReturnUser_whenUserExists() {
        when(dao.findByUsername(USERNAME)).thenReturn(Optional.of(savedUser));

        User actual = service.getByUsername(USERNAME);

        assertThat(actual).isEqualTo(savedUser);
    }

    @Test
    void getByUsername_shouldThrowException_whenUserNotFound() {
        when(dao.findByUsername(NON_EXISTENT_USERNAME)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.getByUsername(NON_EXISTENT_USERNAME));

        assertThat(exception.getMessage()).isEqualTo(String.format(USER_NOT_FOUND_BY_USERNAME, NON_EXISTENT_USERNAME));
    }

    @Test
    void getAll_shouldReturnAllUsers_whenExist() {
        when(dao.findAll()).thenReturn(List.of(savedUser));

        List<User> actual = service.getAll();

        assertThat(actual).hasSize(1);
    }

    @Test
    void getAll_shouldReturnEmptyList_whenNoUsers() {
        when(dao.findAll()).thenReturn(List.of());

        List<User> actual = service.getAll();

        assertThat(actual).isEmpty();
    }

    @Test
    void changePassword_shouldUpdatePassword_whenUserValid() {
        when(dao.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(NEW_PASSWORD)).thenReturn(ENCODED_NEW_PASSWORD);

        service.changePassword(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(dao).update(userCaptor.capture());
        User updatedUser = userCaptor.getValue();
        assertThat(updatedUser.getPassword()).isEqualTo(ENCODED_NEW_PASSWORD);
        assertThat(updatedUser.getUsername()).isEqualTo(USERNAME);
    }

    @Test
    void changePassword_shouldThrowIfUserNotFound() {
        when(dao.findByUsername(NON_EXISTENT_USERNAME)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.changePassword(TestDataProvider.buildInvalidPasswordChangeRequest()));

        assertThat(exception.getMessage()).isEqualTo(String.format(USER_NOT_FOUND_BY_USERNAME, NON_EXISTENT_USERNAME));
    }

    @Test
    void changePassword_shouldCallValidationService() {
        when(dao.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(NEW_PASSWORD)).thenReturn(ENCODED_NEW_PASSWORD);

        service.changePassword(request);

        verify(userInputValidator).validate(request, "Password change request");
    }

    @Test
    void changePassword_shouldThrowValidationException_whenUsernameIsBlank() {
        doThrow(new ValidationFailedException("Username is required")).when(userInputValidator).validate(passwordChangeRequestBlankUsername, "Password change request");

        assertThrows(ValidationFailedException.class, () -> service.changePassword(passwordChangeRequestBlankUsername));

        verify(userInputValidator).validate(passwordChangeRequestBlankUsername, "Password change request");
    }

    @Test
    void changePassword_shouldThrowValidationException_whenNewPasswordTooShort() {
        doThrow(new ValidationFailedException("Password must be between 10 and 100 characters long")).when(userInputValidator).validate(passwordChangeRequestShortNewPassword, "Password change request");

        assertThrows(ValidationFailedException.class, () -> service.changePassword(passwordChangeRequestShortNewPassword));

        verify(userInputValidator).validate(passwordChangeRequestShortNewPassword, "Password change request");
    }

    @Test
    void toggleActive_shouldToggleStatus_whenValid() {
        User inactive = user.toBuilder().isActive(false).build();
        when(dao.findByUsername(USERNAME)).thenReturn(Optional.of(inactive));

        service.toggleActive(TestDataProvider.buildToggleActiveRequest());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(dao).update(userCaptor.capture());
        User updatedUser = userCaptor.getValue();
        assertThat(updatedUser.getIsActive()).isTrue();
        assertThat(updatedUser.getUsername()).isEqualTo(USERNAME);
    }

    @Test
    void toggleActive_shouldThrow_whenUserNotFound() {
        when(dao.findByUsername(NON_EXISTENT_USERNAME)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.toggleActive(TestDataProvider.buildToggleActiveRequestNonExistent()));

        assertThat(exception.getMessage()).isEqualTo(String.format(USER_NOT_FOUND_BY_USERNAME, NON_EXISTENT_USERNAME));
    }

    @Test
    void toggleActive_shouldCallValidationService() {
        User inactive = user.toBuilder().isActive(false).build();
        when(dao.findByUsername(USERNAME)).thenReturn(Optional.of(inactive));

        service.toggleActive(toggleActiveRequestDTO);

        verify(userInputValidator).validate(toggleActiveRequestDTO,  "Toggle active request");
    }

    @Test
    void toggleActive_shouldThrowValidationException_whenUsernameIsBlank() {
        doThrow(new ValidationFailedException("Username is required")).when(userInputValidator).validate(invalidToggleActiveRequest, "Toggle active request");

        ValidationFailedException exception = assertThrows(ValidationFailedException.class, () -> service.toggleActive(invalidToggleActiveRequest));

        assertThat(exception.getMessage()).contains("Username is required");
        verify(userInputValidator).validate(invalidToggleActiveRequest, "Toggle active request");
    }

    @Test
    void toggleActive_shouldThrowValidationFailedException_ifStatusIsAlreadySet() {
        when(dao.findByUsername(USERNAME)).thenReturn(Optional.of(user));

        ValidationFailedException exception = assertThrows(ValidationFailedException.class, () -> service.toggleActive(toggleActiveRequestDTO));

        assertThat(exception.getMessage()).isEqualTo(String.format(USER_ALREADY_ACTIVATED, USERNAME));
        verify(dao, never()).update(any());
    }
}
