package com.gym.crm.dao;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.TrainingType;
import com.gym.crm.model.User;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DatabaseSetup(value = "/dataset/trainer.xml")
class TrainerDAOImplTest extends AbstractDaoTest<TrainerDAO> {
    private static final String INVALID_ID_MESSAGE = "ID must be positive and not null, got: %s";

    @Test
    void save_shouldSaveTrainer_whenValid() {
        Trainer trainer = buildTrainer();

        Trainer actual = dao.save(trainer);

        assertThat(actual.getId()).isNotNull();
        assertThat(dao.findById(actual.getId())).isPresent();
        assertThat(actual.getUser().getUsername()).isEqualTo("Simone.Radcliffe");
        assertThat(actual.getUser().getFirstName()).isEqualTo("Simone");
        assertThat(actual.getUser().getLastName()).isEqualTo("Radcliffe");
        assertThat(actual.getUser().getIsActive()).isTrue();
        assertThat(actual.getSpecialization().getTrainingTypeName()).isEqualTo("Yoga");
    }

    @Test
    void save_shouldThrowException_whenSavingNullTrainer() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> dao.save(null));

        assertThat(exception.getMessage()).isEqualTo("Trainer cannot be null");
    }

    @Test
    void update_shouldUpdateExistingTrainer_whenExists() {
        TrainingType newTrainingType = TrainingType.builder()
                .id(2L)
                .trainingTypeName("Pilates")
                .build();

        Trainer trainer = dao.findById(1L).orElseThrow(() -> new AssertionError("Trainer not found"));
        Trainer updated = trainer.toBuilder()
                .specialization(newTrainingType)
                .build();

        Trainer saved = dao.update(updated);
        Trainer actual = dao.findById(saved.getId()).orElseThrow(() -> new AssertionError("Trainer not found"));

        assertThat(actual.getSpecialization().getTrainingTypeName()).isEqualTo("Pilates");
    }

    @Test
    void update_shouldThrowException_whenIdIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> dao.update(buildTrainer()));

        assertThat(exception.getMessage()).isEqualTo(String.format(INVALID_ID_MESSAGE, "null"));
    }

    @Test
    void findById_shouldReturnTrainer_whenExists() {
        Trainer expected = buildExpectedTrainer();

        Optional<Trainer> actual = dao.findById(1L);

        assertThat(actual).isPresent();
        assertThat(actual.get().getUser().getUsername()).isEqualTo("Callum.Whitfield");
        assertThat(actual).contains(expected);
    }

    @Test
    void findById_shouldReturnEmptyOptional_whenNotFound() {
        Optional<Trainer> actual = dao.findById(999L);

        assertThat(actual).isEmpty();
    }

    @Test
    void findById_shouldThrowException_whenIdIsZero() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> dao.findById(0L));

        assertThat(exception.getMessage()).isEqualTo(String.format(INVALID_ID_MESSAGE, "0"));
    }

    @Test
    void findByUsername_shouldReturnTrainer_whenExists() {
        Trainer expected = buildExpectedTrainer();

        Optional<Trainer> actual = dao.findByUsername("Callum.Whitfield");

        assertThat(actual)
                .isPresent()
                .contains(expected);
    }

    @Test
    void findByUsername_shouldReturnEmptyOptional_whenNotFound() {
        Optional<Trainer> actual = dao.findByUsername("Owen.Castleberry");

        assertThat(actual).isEmpty();
    }

    @Test
    void findByUsername_shouldThrowException_whenUsernameIsBlank() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> dao.findByUsername(" "));

        assertThat(exception.getMessage()).isEqualTo("Username cannot be null or empty");
    }

    @Test
    void findAll_shouldReturnAllTrainers_whenExist() {
        List<Trainer> expected = buildExpectedTrainers();

        List<Trainer> actual = dao.findAll();

        assertThat(actual)
                .hasSize(2)
                .extracting(t -> t.getUser().getUsername())
                .contains("Callum.Whitfield");
        assertThat(actual).containsAll(expected);
    }

    @Test
    void findNotAssignedToTrainee_shouldReturnAllTrainers_whenNoAssigned() {
        List<Trainer> actual = dao.findNotAssignedToTrainee("Petra.Dunmore");

        assertThat(actual)
                .isNotEmpty()
                .extracting(t -> t.getUser().getUsername())
                .containsExactlyInAnyOrder("Callum.Whitfield", "Nora.Pemberton");
        assertThat(actual).containsAll(buildExpectedTrainers());
    }

    @Test
    void findNotAssignedToTrainee_shouldReturnNoTrainers_whenAllAssigned() {
        List<Trainer> actual = dao.findNotAssignedToTrainee("Owen.Castleberry");

        assertThat(actual).isEmpty();
    }

    @Test
    void findNotAssignedToTrainee_shouldReturnAUnassignedTrainers_whenAssigned() {
        List<Trainer> expected = List.of(buildExpectedTrainer());

        List<Trainer> actual = dao.findNotAssignedToTrainee("Ellis.Hargrove");

        assertThat(actual)
                .isNotEmpty()
                .extracting(t -> t.getUser().getUsername())
                .containsExactlyInAnyOrder("Callum.Whitfield");
        assertThat(actual).containsAll(expected);
    }

    private Trainer buildTrainer() {
        return Trainer.builder()
                .user(buildUser())
                .specialization(buildTrainingType())
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

    private TrainingType buildTrainingType() {
        return TrainingType.builder()
                .id(1L)
                .trainingTypeName("Yoga")
                .build();
    }

    private Trainer buildExpectedTrainer() {
        return Trainer.builder()
                .id(1L)
                .user(buildExpectedUser())
                .specialization(buildTrainingType())
                .build();
    }

    private User buildExpectedUser() {
        return User.builder()
                .id(1L)
                .firstName("Callum")
                .lastName("Whitfield")
                .username("Callum.Whitfield")
                .password("pass111")
                .isActive(true)
                .build();
    }

    private List<Trainer> buildExpectedTrainers() {
        User user = User.builder()
                .id(2L)
                .firstName("Nora")
                .lastName("Pemberton")
                .username("Nora.Pemberton")
                .password("pass222")
                .isActive(true)
                .build();
        Trainer trainer = Trainer.builder()
                .id(2L)
                .user(user)
                .specialization(buildTrainingType())
                .build();

        return List.of(buildExpectedTrainer(), trainer);
    }
}
