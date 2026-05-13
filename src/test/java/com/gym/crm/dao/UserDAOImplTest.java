package com.gym.crm.dao;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.gym.crm.model.User;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DatabaseSetup(value = "/dataset/user.xml")
class UserDAOImplTest extends AbstractDaoTest<UserDAO> {
    private static final String INVALID_ID_MESSAGE = "ID must be positive and not null, got: %s";
    private static final String INVALID_USERNAME_MESSAGE = "Username cannot be null or empty";

    @Test
    void save_shouldSaveUser_whenValid() {
        User user = buildUser();

        User actual = dao.save(user);

        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getUsername()).isEqualTo("Simone.Radcliffe");
        assertThat(actual.getFirstName()).isEqualTo("Simone");
        assertThat(actual.getLastName()).isEqualTo("Radcliffe");
        assertThat(actual.getIsActive()).isTrue();
    }

    @Test
    void save_shouldThrowException_whenSavingNullUser() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> dao.save(null));

        assertThat(exception.getMessage()).isEqualTo("User cannot be null");
    }

    @Test
    void update_shouldUpdateExistingUser_whenExists() {
        User user = dao.findById(1L).orElseThrow(() -> new AssertionError("User not found"));
        User updated = user.toBuilder().isActive(false).build();

        User saved = dao.update(updated);
        User actual = dao.findById(saved.getId()).orElseThrow(() -> new AssertionError("User not found"));

        assertThat(actual.getIsActive()).isFalse();
    }

    @Test
    void update_shouldThrowException_whenIdIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> dao.update(buildUser()));

        assertThat(exception.getMessage()).isEqualTo(String.format(INVALID_ID_MESSAGE, "null"));
    }

    @Test
    void findById_shouldReturnUser_whenExists() {
        User expected = buildExpectedUserCallum();

        Optional<User> actual = dao.findById(1L);

        assertThat(actual).isPresent();
        assertThat(actual.get().getUsername()).isEqualTo("Callum.Whitfield");
        assertThat(actual).contains(expected);
    }

    @Test
    void findById_shouldReturnEmptyOptional_whenNotFound() {
        Optional<User> actual = dao.findById(999L);

        assertThat(actual).isEmpty();
    }

    @Test
    void findById_shouldThrowException_whenIdIsZero() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> dao.findById(0L));

        assertThat(exception.getMessage()).isEqualTo(String.format(INVALID_ID_MESSAGE, "0"));
    }

    @Test
    void findByUsername_shouldReturnEmptyOptional_whenNotFound() {
        Optional<User> actual = dao.findByUsername("Owen.Castleberry");

        assertThat(actual).isEmpty();
    }

    @Test
    void findByUsername_shouldThrowException_whenUsernameIsBlank() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> dao.findByUsername(" "));

        assertThat(exception.getMessage()).isEqualTo(INVALID_USERNAME_MESSAGE);
    }

    @Test
    void findByUsername_shouldThrowException_whenUsernameIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> dao.findByUsername(null));

        assertThat(exception.getMessage()).isEqualTo(INVALID_USERNAME_MESSAGE);
    }

    @Test
    void findAll_shouldReturnAllUsers_whenExist() {
        List<User> expected = buildExpectedUsers();

        List<User> actual = dao.findAll();

        assertThat(actual)
                .hasSize(3)
                .extracting(User::getUsername)
                .containsExactlyInAnyOrder("Callum.Whitfield", "Nora.Pemberton", "Ellis.Hargrove");
        assertThat(actual).containsAll(expected);
    }

    @Test
    void existsByUsername_shouldReturnTrue_whenUserExists() {
        boolean result = dao.existsByUsername("Callum.Whitfield");

        assertThat(result).isTrue();
    }

    @Test
    void existsByUsername_shouldReturnFalse_whenUserDoesNotExist() {
        boolean result = dao.existsByUsername("non.existent");

        assertThat(result).isFalse();
    }

    @Test
    void existsByUsername_shouldThrowException_whenUsernameIsBlank() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> dao.existsByUsername(" "));

        assertThat(exception.getMessage()).isEqualTo(INVALID_USERNAME_MESSAGE);
    }

    @Test
    void existsByUsername_shouldThrowException_whenUsernameIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> dao.existsByUsername(null));

        assertThat(exception.getMessage()).isEqualTo(INVALID_USERNAME_MESSAGE);
    }

    private User buildUser() {
        return User.builder()
                .firstName("Simone")
                .lastName("Radcliffe")
                .username("Simone.Radcliffe")
                .password("pass444")
                .isActive(true)
                .build();
    }

    private User buildExpectedUserCallum() {
        return User.builder()
                .id(1L)
                .firstName("Callum")
                .lastName("Whitfield")
                .username("Callum.Whitfield")
                .password("pass111")
                .isActive(true)
                .build();
    }

    private User buildExpectedUserNora() {
        return User.builder()
                .id(2L)
                .firstName("Nora")
                .lastName("Pemberton")
                .username("Nora.Pemberton")
                .password("pass222")
                .isActive(true)
                .build();
    }

    private User buildExpectedUserEllis() {
        return User.builder()
                .id(3L)
                .firstName("Ellis")
                .lastName("Hargrove")
                .username("Ellis.Hargrove")
                .password("pass333")
                .isActive(true)
                .build();
    }

    private List<User> buildExpectedUsers() {
        return List.of(buildExpectedUserCallum(),
                buildExpectedUserNora(),
                buildExpectedUserEllis());
    }
}
