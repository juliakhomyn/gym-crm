package com.gym.crm.service;

import com.gym.crm.dao.UserDAO;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.model.User;
import com.gym.crm.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    private static final String USERNAME = "Owen.Castleberry";
    private static final String OLD_PASSWORD = "oldPassword";
    private static final String NEW_PASSWORD = "newPassword";
    private static final long VALID_ID = 1L;
    private static final long NOT_FOUND_ID = 999L;
    private static final String ENCODED_NEW_PASSWORD = "encodedPassword";
    
    private static final String USER_NOT_FOUND_BY_USERNAME = "User not found by username: %s";
    private static final String USER_NOT_FOUND_BY_ID = "User not found by id: %s";

    @Mock
    private UserDAO dao;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl service;

    private User user;
    private User savedUser;

    @BeforeEach
    void setUp() {
        savedUser = buildUser();
    }

    @Test
    void getUById_shouldReturnUser_whenUserExists() {
        when(dao.findById(VALID_ID)).thenReturn(Optional.of(savedUser));

        User actual = service.getById(VALID_ID);

        assertThat(actual).isEqualTo(savedUser);
    }

    @Test
    void getById_shouldThrowException_whenUserNotFound() {
        when(dao.findById(NOT_FOUND_ID)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> service.getById(NOT_FOUND_ID));

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
        when(dao.findByUsername("Non.Existent")).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> service.getByUsername("Non.Existent"));

        assertThat(exception.getMessage()).isEqualTo(String.format(USER_NOT_FOUND_BY_USERNAME, "Non.Existent"));
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
        User user = buildUser();

        when(dao.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(NEW_PASSWORD)).thenReturn(ENCODED_NEW_PASSWORD);
        service.changePassword(USERNAME, OLD_PASSWORD, NEW_PASSWORD);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(dao).update(userCaptor.capture());
        User updatedUser = userCaptor.getValue();
        assertThat(updatedUser.getPassword()).isEqualTo(ENCODED_NEW_PASSWORD);
        assertThat(updatedUser.getUsername()).isEqualTo(USERNAME);
    }

    @Test
    void changePassword_shouldThrowIfUserNotFound() {
        String username = "Non.Existent";

        when(dao.findByUsername(username)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.changePassword(username, OLD_PASSWORD, NEW_PASSWORD));

        assertThat(exception.getMessage()).isEqualTo(String.format(USER_NOT_FOUND_BY_USERNAME, username));
    }

    @Test
    void toggleActive_shouldToggleStatus() {
        User user = buildUser();

        when(dao.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        service.toggleActive(USERNAME);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(dao).update(userCaptor.capture());
        User updatedUser = userCaptor.getValue();
        assertThat(updatedUser.getIsActive()).isFalse();
        assertThat(updatedUser.getUsername()).isEqualTo(USERNAME);
    }

    @Test
    void toggleActive_shouldThrowIfUserNotFound() {
        String username = "Non.Existent";

        when(dao.findByUsername(username)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.toggleActive(username));

        assertThat(exception.getMessage()).isEqualTo(String.format(USER_NOT_FOUND_BY_USERNAME, username));
    }

    private User buildUser() {
        return User.builder()
                .id(VALID_ID)
                .username(USERNAME)
                .password(OLD_PASSWORD)
                .isActive(true)
                .build();
    }
}
