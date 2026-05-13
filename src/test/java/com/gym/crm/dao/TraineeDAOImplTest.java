package com.gym.crm.dao;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DatabaseSetup(value = "/dataset/trainee.xml")
class TraineeDAOImplTest extends AbstractDaoTest<TraineeDAO> {
    private static final String INVALID_ID_MESSAGE = "ID must be positive and not null, got: %s";
    private static final String NULL_OR_EMPTY_USERNAME_MESSAGE = "Username cannot be null or empty";

    @Autowired
    private TrainerDAO trainerDao;

    @Test
    void save_shouldSaveTrainee_whenValid() {
        Trainee trainee = buildTrainee();

        Trainee actual = dao.save(trainee);

        assertThat(actual.getId()).isNotNull();
        assertThat(dao.findById(actual.getId())).isPresent();
        assertThat(actual.getUser().getUsername()).isEqualTo("Simone.Radcliffe");
        assertThat(actual.getUser().getFirstName()).isEqualTo("Simone");
        assertThat(actual.getUser().getLastName()).isEqualTo("Radcliffe");
        assertThat(actual.getUser().getIsActive()).isTrue();
        assertThat(actual.getDateOfBirth()).isEqualTo(LocalDate.of(2000, 3, 10));
        assertThat(actual.getAddress()).isEqualTo("123 Main St");
    }

    @Test
    void save_shouldThrowException_whenSavingNullTrainee() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> dao.save(null));

        assertThat(exception.getMessage()).isEqualTo("Trainee cannot be null");
    }

    @Test
    void update_shouldUpdateExistingTrainee_whenExists() {
        Trainee trainee = dao.findById(1L).orElseThrow(() -> new AssertionError("Trainee not found"));
        Trainee updated = trainee.toBuilder().address("new address").build();

        Trainee saved = dao.update(updated);
        Trainee actual = dao.findById(saved.getId()).orElseThrow(() -> new AssertionError("Trainee not found"));

        assertThat(actual.getAddress()).isEqualTo("new address");
    }

    @Test
    void update_shouldThrowException_whenIdIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> dao.update(buildTrainee()));

        assertThat(exception.getMessage()).isEqualTo(String.format(INVALID_ID_MESSAGE, "null"));
    }

    @Test
    void delete_shouldDeleteTrainee_whenExists() {
        dao.delete(1L);

        assertThat(dao.findById(1L)).isEmpty();
        assertThat(dao.findAll()).hasSize(1);
    }

    @Test
    void delete_shouldThrowException_whenIdIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> dao.delete(null));

        assertThat(exception.getMessage()).isEqualTo(String.format(INVALID_ID_MESSAGE, "null"));
    }

    @Test
    void deleteByUsername_shouldDeleteTrainee_whenExists() {
        dao.deleteByUsername("Nora.Pemberton");

        assertThat(dao.findById(1L)).isEmpty();
        assertThat(dao.findAll().size()).isEqualTo(1);
    }

    @Test
    void deleteByUsername_shouldThrowException_whenIdIsBlank() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> dao.deleteByUsername(" "));

        assertThat(exception.getMessage()).isEqualTo(NULL_OR_EMPTY_USERNAME_MESSAGE);
    }

    @Test
    void deleteByUsername_shouldThrowException_whenIdIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> dao.deleteByUsername(null));

        assertThat(exception.getMessage()).isEqualTo(NULL_OR_EMPTY_USERNAME_MESSAGE);
    }

    @Test
    void findById_shouldReturnTrainee_whenExists() {
        Trainee expected = buildExpectedTrainee();

        Optional<Trainee> actual = dao.findById(1L);

        assertThat(actual).isPresent();
        assertThat(actual.get().getUser().getUsername()).isEqualTo("Nora.Pemberton");
        assertThat(actual).contains(expected);
    }

    @Test
    void findById_shouldReturnEmptyOptional_whenNotFound() {
        Optional<Trainee> actual = dao.findById(999L);

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
        Optional<Trainee> actual = dao.findByUsername("Owen.Castleberry");

        assertThat(actual).isEmpty();
    }

    @Test
    void findByUsername_shouldThrowException_whenUsernameIsBlank() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> dao.findByUsername(" "));

        assertThat(exception.getMessage()).isEqualTo(NULL_OR_EMPTY_USERNAME_MESSAGE);
    }

    @Test
    void findByUsername_shouldThrowException_whenUsernameIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> dao.findByUsername(null));

        assertThat(exception.getMessage()).isEqualTo(NULL_OR_EMPTY_USERNAME_MESSAGE);
    }

    @Test
    void findAll_shouldReturnAllTrainees_whenExist() {
        List<Trainee> expected = buildExpectedTrainees();

        List<Trainee> actual = dao.findAll();

        assertThat(actual)
                .hasSize(2)
                .extracting(t -> t.getUser().getUsername())
                .containsExactlyInAnyOrder("Nora.Pemberton", "Ellis.Hargrove");
        assertThat(actual).containsAll(expected);
    }

    @Test
    void findByUsername_shouldReturnTrainee_whenExists() {
        Trainee expected = buildExpectedTrainee();

        Optional<Trainee> actual = dao.findByUsername("Nora.Pemberton");

        assertThat(actual)
                .isPresent()
                .contains(expected);
    }

    @Test
    void updateTrainersList_shouldThrowException_whenUsernameIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.updateTrainersList(null, List.of()));
    }

    @Test
    void updateTrainersList_shouldThrowException_whenUsernameIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.updateTrainersList("  ", List.of()));
    }

    @Test
    void updateTrainersList_shouldThrowException_whenTrainersIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.updateTrainersList("Nora.Pemberton", null));
    }

    @Test
    void updateTrainersList_shouldThrowException_whenTraineeNotFound() {
        assertThrows(IllegalArgumentException.class,
                () -> dao.updateTrainersList("NonExistent.User", List.of()));
    }

    @Test
    void updateTrainersList_shouldReplaceWithSingleTrainer_whenUpdatingWithOneTrainerAssignedBefore() {
        String username = "Nora.Pemberton";
        Trainer expectedTrainer = findTrainerById(2L);
        List<Trainer> newTrainers = List.of(expectedTrainer);

        dao.updateTrainersList(username, newTrainers);

        Trainee updated = dao.findByUsernameWithTrainers(username).orElseThrow();
        assertThat(updated.getTrainers()).hasSize(1);
        assertThat(updated.getTrainers())
                .containsExactlyInAnyOrderElementsOf(newTrainers);
    }

    @Test
    void updateTrainersList_shouldReplaceWithMultipleTrainers_whenUpdatingWithMultipleTrainers() {
        String username = "Nora.Pemberton";
        Trainer trainer1 = findTrainerById(1L);
        Trainer trainer2 = findTrainerById(2L);
        List<Trainer> newTrainers = List.of(trainer1, trainer2);

        dao.updateTrainersList(username, newTrainers);

        Trainee updated = dao.findByUsernameWithTrainers(username).orElseThrow();
        assertThat(updated.getTrainers()).hasSize(2);
        assertThat(updated.getTrainers())
                .containsExactlyInAnyOrderElementsOf(newTrainers);
    }

    @Test
    void updateTrainersList_shouldClearTrainers_whenEmptyListProvided() {
        String username = "Nora.Pemberton";

        dao.updateTrainersList(username, List.of());

        Trainee updated = dao.findByUsernameWithTrainers(username).orElseThrow();
        assertThat(updated.getTrainers()).isEmpty();
    }

    @Test
    void updateTrainersList_shouldReplaceWithNewTrainer_whenUpdatingWithNewTrainer() {
        String username = "Ellis.Hargrove";
        Trainer trainer = findTrainerById(3L);
        List<Trainer> newTrainers = List.of(trainer);

        dao.updateTrainersList(username, newTrainers);

        Trainee updated = dao.findByUsernameWithTrainers(username).orElseThrow();
        assertThat(updated.getTrainers()).hasSize(1);
        assertThat(updated.getTrainers())
                .containsExactlyInAnyOrderElementsOf(newTrainers);
    }

    private Trainee buildTrainee() {
        return Trainee.builder()
                .user(buildUser())
                .dateOfBirth(LocalDate.of(2000, 3, 10))
                .address("123 Main St")
                .build();
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

    private Trainee buildExpectedTrainee() {
        return Trainee.builder()
                .id(1L)
                .user(buildExpectedUser())
                .dateOfBirth(LocalDate.of(2000, 3, 10))
                .address("123 Main St")
                .build();
    }

    private User buildExpectedUser() {
        return User.builder()
                .id(2L)
                .firstName("Nora")
                .lastName("Pemberton")
                .username("Nora.Pemberton")
                .password("pass222")
                .isActive(true)
                .build();
    }

    private List<Trainee> buildExpectedTrainees() {
        User user = User.builder()
                .id(3L)
                .firstName("Ellis")
                .lastName("Hargrove")
                .username("Ellis.Hargrove")
                .password("pass222")
                .isActive(true)
                .build();
        Trainee trainee = Trainee.builder()
                .id(2L)
                .user(user)
                .dateOfBirth(LocalDate.of(2002, 7, 15))
                .address("567 Oak St")
                .build();

        return List.of(buildExpectedTrainee(), trainee);
    }

    private Trainer findTrainerById(Long id) {
        return trainerDao.findById(id).orElseThrow();
    }
}
