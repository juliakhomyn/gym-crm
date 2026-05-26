package com.gym.crm.repository;

import com.github.database.rider.core.api.dataset.DataSet;
import com.gym.crm.model.Trainer;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataSet(value = "/dataset/trainer.xml", cleanBefore = true)
public class TrainerRepositoryTest extends AbstractRepositoryTest<TrainerRepository> {
    private static final String USERNAME = "Callum.Whitfield";
    private static final String TRAINEE_USERNAME = "Owen.Castleberry";

    @Test
    void findByUserUsername_shouldReturnTrainer_whenExists() {
        Optional<Trainer> actual = repository.findByUserUsername(USERNAME);

        assertThat(actual).isPresent();
        assertThat(actual.get().getUser().getUsername()).isEqualTo(USERNAME);
    }

    @Test
    void findByUserUsername_shouldReturnEmptyOptional_whenNotFound() {
        Optional<Trainer> actual = repository.findByUserUsername(TRAINEE_USERNAME);

        assertThat(actual).isEmpty();
    }

    @Test
    void findByUsernameWithTrainees_shouldReturnTrainerWithTrainees_whenExists() {
        Optional<Trainer> actual = repository.findByUsernameWithTrainees(USERNAME);

        assertThat(actual).isPresent();
        assertThat(actual.get().getUser().getUsername()).isEqualTo(USERNAME);
        assertThat(actual.get().getTrainees())
                .extracting("user.username")
                .containsExactlyInAnyOrder(TRAINEE_USERNAME);
    }

    @Test
    void findByUsernameWithTrainees_shouldReturnEmptyOptional_whenNotFound() {
        Optional<Trainer> actual = repository.findByUsernameWithTrainees("Not.Found");

        assertThat(actual).isEmpty();
    }

    @Test
    void findNotAssignedToTrainee_shouldReturnAllTrainers_whenNoneAssigned() {
        List<Trainer> actual = repository.findNotAssignedToTrainee("Petra.Dunmore");

        assertThat(actual)
                .extracting("user.username")
                .containsExactlyInAnyOrder(USERNAME, "Nora.Pemberton");
    }

    @Test
    void findNotAssignedToTrainee_shouldReturnEmptyList_whenAllAssigned() {
        List<Trainer> actual = repository.findNotAssignedToTrainee(TRAINEE_USERNAME);

        assertThat(actual).isEmpty();
    }

    @Test
    void findNotAssignedToTrainee_shouldReturnUnassignedTrainers_whenSomeAssigned() {
        List<Trainer> actual = repository.findNotAssignedToTrainee("Ellis.Hargrove");

        assertThat(actual)
                .extracting(t -> t.getUser().getUsername())
                .containsExactlyInAnyOrder(USERNAME);
    }
}
