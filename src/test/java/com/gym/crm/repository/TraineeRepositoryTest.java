package com.gym.crm.repository;

import com.github.database.rider.core.api.dataset.DataSet;
import com.gym.crm.model.Trainee;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataSet(value = "/dataset/trainee.xml", cleanBefore = true)
class TraineeRepositoryTest extends AbstractRepositoryTest<TraineeRepository> {
    private static final String USERNAME = "Nora.Pemberton";

    @Test
    void findByUsername_shouldReturnTrainee_whenExists() {
        Optional<Trainee> actual = repository.findByUserUsername(USERNAME);

        assertThat(actual).isPresent();
        assertThat(actual.get().getUser().getUsername()).isEqualTo(USERNAME);
    }

    @Test
    void findByUsername_shouldReturnEmptyOptional_whenNotFound() {
        Optional<Trainee> actual = repository.findByUserUsername("Owen.Castleberry");

        assertThat(actual).isEmpty();
    }

    @Test
    void findByUsernameWithTrainers_shouldReturnTraineeWithTrainers_whenExists() {
        Optional<Trainee> actual = repository.findByUsernameWithTrainers(USERNAME);

        assertThat(actual).isPresent();
        assertThat(actual.get().getTrainers()).isNotNull();
    }
}
