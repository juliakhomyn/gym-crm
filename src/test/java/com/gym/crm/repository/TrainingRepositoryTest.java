package com.gym.crm.repository;

import com.github.database.rider.core.api.dataset.DataSet;
import com.gym.crm.exception.ValidationFailedException;
import com.gym.crm.model.Training;
import com.gym.crm.search.filter.TraineeTrainingFilter;
import com.gym.crm.search.filter.TrainerTrainingFilter;
import com.gym.crm.testutils.TestDataProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataSet(value = "/dataset/training.xml", cleanBefore = true)
public class TrainingRepositoryTest extends AbstractRepositoryTest<TrainingRepository> {
    private static final String TRAINER_USERNAME = "Owen.Castleberry";
    private static final String TRAINEE_USERNAME1 = "Simone.Radcliffe";
    private static final String YOGA = "Yoga";

    @Test
    void findById_shouldReturnTraining_whenExists() {
        Training expected = TestDataProvider.buildExpectedTraining();

        Optional<Training> actual = repository.findById(1L);

        assertThat(actual).isPresent();
        assertThat(actual.get().getTrainingName()).isEqualTo("Hot Yoga");
        assertThat(actual.get().getTrainingDate()).isEqualTo(LocalDate.of(2026, 4, 15));
        assertThat(actual).contains(expected);
    }

    @Test
    void findById_shouldReturnEmptyOptional_whenNotFound() {
        Optional<Training> actual = repository.findById(999L);

        assertThat(actual).isEmpty();
    }

    @Test
    void findAll_shouldReturnAllTrainings_whenExist() {
        List<Training> expected = TestDataProvider.buildExpectedTrainings();

        List<Training> actual = repository.findAll();

        assertThat(actual)
                .isNotEmpty()
                .containsAll(expected);
        assertThat(actual)
                .hasSize(2)
                .extracting(Training::getTrainingName)
                .containsExactly("Hot Yoga", "Hot Yoga");
        assertThat(actual)
                .extracting(Training::getTrainingDate)
                .containsExactlyInAnyOrder(LocalDate.of(2026, 4, 20), LocalDate.of(2026, 4, 15));
        assertThat(actual)
                .extracting(Training::getTrainingDuration)
                .contains(60);
    }

    @Test
    void findByTraineeCriteria_shouldThrowException_whenNullFilter() {
        ValidationFailedException exception = assertThrows(ValidationFailedException.class,
                () -> repository.findByTraineeCriteria(null));

        assertThat(exception.getMessage()).isEqualTo("Filter cannot be null");
    }

    @Test
    void findByTraineeCriteria_shouldThrowException_whenNoUsername() {
        TraineeTrainingFilter filter = TraineeTrainingFilter.builder().build();
        ValidationFailedException exception = assertThrows(ValidationFailedException.class,
                () -> repository.findByTraineeCriteria(filter));

        assertThat(exception.getMessage()).isEqualTo("Username cannot be null or empty");
    }

    @Test
    void findByTraineeCriteria_shouldReturnEmptyList_whenNonExistingUsername() {
        TraineeTrainingFilter filter = TraineeTrainingFilter.builder().username("Non-Existing Username").build();

        List<Training> actual = repository.findByTraineeCriteria(filter);

        assertThat(actual).isEmpty();
    }

    @Test
    void findByTrainerCriteria_shouldThrowException_whenNullFilter() {
        ValidationFailedException exception = assertThrows(ValidationFailedException.class,
                () -> repository.findByTrainerCriteria(null));

        assertThat(exception.getMessage()).isEqualTo("Filter cannot be null");
    }

    @Test
    void findByTrainerCriteria_shouldThrowException_whenNoUsername() {
        TrainerTrainingFilter filter = TrainerTrainingFilter.builder().build();
        ValidationFailedException exception = assertThrows(ValidationFailedException.class,
                () -> repository.findByTrainerCriteria(filter));

        assertThat(exception.getMessage()).isEqualTo("Username cannot be null or empty");
    }

    @Test
    void findByTrainerCriteria_shouldReturnEmptyList_whenNonExistingUsername() {
        TrainerTrainingFilter filter = TrainerTrainingFilter.builder().username("Non-Existing Username").build();

        List<Training> actual = repository.findByTrainerCriteria(filter);

        assertThat(actual).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("traineeFilterProviderExisting")
    void findByTraineeCriteria_shouldReturnCorrectTrainings_whenExist(TraineeTrainingFilter filter, int expectedSize, List<Long> expectedIds) {
        List<Training> actual = repository.findByTraineeCriteria(filter);

        assertThat(actual).hasSize(expectedSize);
        assertThat(actual)
                .extracting("id")
                .containsExactlyInAnyOrderElementsOf(expectedIds);
    }

    @ParameterizedTest
    @MethodSource("traineeFilterProviderNonExisting")
    void findByTraineeCriteria_shouldReturnCorrectTrainings_whenNotExist(TraineeTrainingFilter filter) {
        List<Training> actual = repository.findByTraineeCriteria(filter);

        assertThat(actual).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("trainerFilterProviderExisting")
    void findByTrainerCriteria_shouldReturnCorrectTrainings_whenExist(TrainerTrainingFilter filter, int expectedSize, List<Long> expectedIds) {
        List<Training> actual = repository.findByTrainerCriteria(filter);

        assertThat(actual).hasSize(expectedSize);
        assertThat(actual)
                .extracting("id")
                .containsExactlyInAnyOrderElementsOf(expectedIds);
    }

    @ParameterizedTest
    @MethodSource("trainerFilterProviderNonExisting")
    void findByTrainerCriteria_shouldReturnCorrectTrainings_whenNotExist(TrainerTrainingFilter filter) {
        List<Training> actual = repository.findByTrainerCriteria(filter);

        assertThat(actual).isEmpty();
    }

    private static Stream<Arguments> traineeFilterProviderExisting() {
        return Stream.of(
                Arguments.of(TraineeTrainingFilter.builder()
                                .username(TRAINEE_USERNAME1)
                                .build(),
                        1,
                        List.of(1L)),
                Arguments.of(TraineeTrainingFilter.builder()
                                .username("Ellis.Hargrove")
                                .build(),
                        1,
                        List.of(2L)),
                Arguments.of(TraineeTrainingFilter.builder()
                                .username(TRAINEE_USERNAME1)
                                .fromDate(LocalDate.of(2026, 4, 1))
                                .toDate(LocalDate.of(2026, 4, 30))
                                .build(),
                        1,
                        List.of(1L)),
                Arguments.of(TraineeTrainingFilter.builder()
                                .username(TRAINEE_USERNAME1)
                                .trainingTypeName(YOGA)
                                .build(),
                        1,
                        List.of(1L)),
                Arguments.of(TraineeTrainingFilter.builder()
                                .username(TRAINEE_USERNAME1)
                                .joinFullName("Owen Castleberry")
                                .build(),
                        1,
                        List.of(1L))
        );
    }

    private static Stream<Arguments> traineeFilterProviderNonExisting() {
        return Stream.of(
                Arguments.of(TraineeTrainingFilter.builder()
                        .username(TRAINEE_USERNAME1)
                        .fromDate(LocalDate.of(2020, 1, 1))
                        .toDate(LocalDate.of(2020, 12, 31))
                        .build()),
                Arguments.of(TraineeTrainingFilter.builder()
                        .username(TRAINEE_USERNAME1)
                        .trainingTypeName("Cardio")
                        .build()),
                Arguments.of(TraineeTrainingFilter.builder()
                        .username(TRAINEE_USERNAME1)
                        .trainingTypeName("Cardio")
                        .build()),
                Arguments.of(TraineeTrainingFilter.builder()
                        .username(TRAINEE_USERNAME1)
                        .joinFullName(TRAINER_USERNAME)
                        .fromDate(LocalDate.of(2026, 4, 16))
                        .toDate(LocalDate.of(2026, 4, 30))
                        .trainingTypeName(YOGA)
                        .build())
        );
    }

    private static Stream<Arguments> trainerFilterProviderExisting() {
        return Stream.of(
                Arguments.of(TrainerTrainingFilter.builder()
                                .username(TRAINER_USERNAME)
                                .build(),
                        2,
                        List.of(1L, 2L)),
                Arguments.of(TrainerTrainingFilter.builder()
                                .username(TRAINER_USERNAME)
                                .joinFullName("Simone Radcliffe")
                                .build(),
                        1,
                        List.of(1L)),
                Arguments.of(TrainerTrainingFilter.builder()
                                .username(TRAINER_USERNAME)
                                .joinFullName("Ellis Hargrove")
                                .build(),
                        1,
                        List.of(2L)),
                Arguments.of(TrainerTrainingFilter.builder()
                                .username(TRAINER_USERNAME)
                                .fromDate(LocalDate.of(2026, 4, 16))
                                .build(),
                        1,
                        List.of(2L)),
                Arguments.of(TrainerTrainingFilter.builder()
                                .username(TRAINER_USERNAME)
                                .toDate(LocalDate.of(2026, 4, 18))
                                .build(),
                        1,
                        List.of(1L)),
                Arguments.of(TrainerTrainingFilter.builder()
                                .username(TRAINER_USERNAME)
                                .fromDate(LocalDate.of(2026, 4, 14))
                                .toDate(LocalDate.of(2026, 4, 16))
                                .build(),
                        1,
                        List.of(1L)),
                Arguments.of(TrainerTrainingFilter.builder()
                                .username(TRAINER_USERNAME)
                                .joinFullName("Ellis Hargrove")
                                .fromDate(LocalDate.of(2026, 4, 19))
                                .build(),
                        1,
                        List.of(2L))
        );
    }

    private static Stream<Arguments> trainerFilterProviderNonExisting() {
        return Stream.of(
                Arguments.of(TrainerTrainingFilter.builder()
                        .username(TRAINER_USERNAME)
                        .joinFullName("NonExistent")
                        .build()),
                Arguments.of(TrainerTrainingFilter.builder()
                        .username(TRAINER_USERNAME)
                        .fromDate(LocalDate.of(2026, 4, 21))
                        .build()),
                Arguments.of(TrainerTrainingFilter.builder()
                        .username(TRAINER_USERNAME)
                        .joinFullName("Simone Radcliffe")
                        .fromDate(LocalDate.of(2026, 4, 16))
                        .build())
        );
    }
}
