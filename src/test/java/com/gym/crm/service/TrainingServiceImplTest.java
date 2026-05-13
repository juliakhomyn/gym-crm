package com.gym.crm.service;

import com.gym.crm.dao.TraineeDAO;
import com.gym.crm.dao.TrainerDAO;
import com.gym.crm.dao.TrainingDAO;
import com.gym.crm.dto.training.TrainingRequestDTO;
import com.gym.crm.dto.training.TrainingResponseDTO;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.exception.ValidationFailedException;
import com.gym.crm.mapper.TrainingMapper;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import com.gym.crm.model.User;
import com.gym.crm.search.filter.TraineeTrainingFilter;
import com.gym.crm.search.filter.TrainerTrainingFilter;
import com.gym.crm.service.common.UserInputValidator;
import com.gym.crm.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {
    private static final String TRAINEE_USERNAME = "Callum.Whitfield";
    private static final String TRAINER_USERNAME = "Nora.Pemberton";
    private static final String TRAINING_NAME = "Morning Cardio";
    private static final String TRAINING_TYPE_NAME = "Cardio";
    private static final long VALID_ID = 1L;
    private static final long INVALID_ID = -1L;
    private static final long NOT_FOUND_ID = 999L;

    private static final String TRAINING_CANNOT_BE_NULL = "Training cannot be null";
    private static final String TRAINING_NOT_FOUND_BY_ID = "Training not found by id: %s";
    private static final String ID_CANNOT_BE_NULL = "ID cannot be null";
    private static final String ID_CANNOT_BE_NEGATIVE = "ID must be a positive number";

    @Mock
    private TrainingDAO dao;
    @Mock
    private TraineeDAO traineeDAO;
    @Mock
    private TrainerDAO trainerDAO;
    @Mock
    private TrainingMapper mapper;
    @Mock
    private UserInputValidator userInputValidator;

    @InjectMocks
    private TrainingServiceImpl service;

    private Trainee trainee;
    private Trainer trainer;
    private Training savedTraining;
    private TrainingRequestDTO request;
    private TrainingResponseDTO response;

    @BeforeEach
    void setUp() {
        trainee = buildTrainee();
        trainer = buildTrainer();
        savedTraining = buildTraining().toBuilder()
                .id(VALID_ID)
                .build();
        request = buildTrainingRequestDTO();
        response = buildTrainingResponseDTO();
    }

    @Test
    void createTraining_shouldSaveTrainingWithCredentials() {
        when(mapper.toEntity(request)).thenReturn(savedTraining);
        when(dao.save(any(Training.class))).thenReturn(savedTraining);
        when(mapper.toDto(savedTraining)).thenReturn(response);
        when(traineeDAO.findByUsername(TRAINEE_USERNAME)).thenReturn(Optional.ofNullable(trainee));
        when(trainerDAO.findByUsername(TRAINER_USERNAME)).thenReturn(Optional.ofNullable(trainer));

        TrainingResponseDTO actual = service.createTraining(request);

        assertThat(actual).isEqualTo(response);
        verify(mapper).toEntity(request);
        verify(mapper).toDto(savedTraining);
        verify(userInputValidator).validate(request, "Training");
        verify(dao).save(any(Training.class));
    }

    @Test
    void createTraining_shouldThrowException_whenTrainingIsNull() {
        doThrow(new ValidationFailedException(TRAINING_CANNOT_BE_NULL)).when(userInputValidator).validate(null, "Training");

        ValidationFailedException exception = assertThrows(ValidationFailedException.class, () -> service.createTraining(null));

        assertThat(exception.getMessage()).isEqualTo(TRAINING_CANNOT_BE_NULL);
    }

    @Test
    void getTrainingById_shouldReturnTraining_whenTrainingExists() {
        when(dao.findById(VALID_ID)).thenReturn(Optional.of(savedTraining));
        when(mapper.toDto(savedTraining)).thenReturn(response);

        TrainingResponseDTO actual = service.getTrainingById(VALID_ID);

        assertThat(actual).isEqualTo(response);
    }

    @Test
    void getTrainingById_shouldThrowException_whenTrainingNotFound() {
        when(dao.findById(NOT_FOUND_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.getTrainingById(NOT_FOUND_ID));

        assertThat(exception.getMessage()).isEqualTo(String.format(TRAINING_NOT_FOUND_BY_ID, NOT_FOUND_ID));
    }

    @Test
    void getTrainingById_shouldThrow_whenIdIsNull() {
        doThrow(new ValidationFailedException(ID_CANNOT_BE_NULL)).when(userInputValidator).validateId(null);

        ValidationFailedException exception = assertThrows(ValidationFailedException.class, () -> service.getTrainingById(null));

        assertThat(exception.getMessage()).isEqualTo(ID_CANNOT_BE_NULL);
    }

    @Test
    void getTrainingById_shouldThrow_whenIdIsNegative() {
        doThrow(new ValidationFailedException(ID_CANNOT_BE_NEGATIVE)).when(userInputValidator).validateId(INVALID_ID);

        ValidationFailedException exception = assertThrows(ValidationFailedException.class, () -> service.getTrainingById(INVALID_ID));

        assertThat(exception.getMessage()).isEqualTo(ID_CANNOT_BE_NEGATIVE);
    }

    @Test
    void getAllTrainings_shouldReturnAllTrainings_whenExist() {
        when(dao.findAll()).thenReturn(List.of(savedTraining));
        when(mapper.toDto(savedTraining)).thenReturn(response);

        List<TrainingResponseDTO> actual = service.getAllTrainings();

        assertThat(actual).hasSize(1);
    }

    @Test
    void getAllTrainings_shouldReturnEmptyList_whenNoTrainings() {
        when(dao.findAll()).thenReturn(List.of());

        List<TrainingResponseDTO> actual = service.getAllTrainings();

        assertThat(actual).isEmpty();
    }

    @Test
    void getTraineeTrainings_shouldReturnList_whenValidFilter() {
        TraineeTrainingFilter filter = buildTraineeTrainingFilter();
        Training training = buildTraining();
        List<Training> trainings = List.of(training);
        TrainingResponseDTO expected = buildTrainingResponseDTO();

        when(dao.findByTraineeCriteria(filter)).thenReturn(trainings);
        when(mapper.toDto(training)).thenReturn(expected);

        List<TrainingResponseDTO> actual = service.getTraineeTrainings(filter);

        assertThat(actual)
                .hasSize(1)
                .contains(expected);
        verify(dao).findByTraineeCriteria(filter);
        verify(mapper).toDto(training);
    }

    @Test
    void getTrainerTrainings_shouldReturnList_whenValidFilter() {
        TrainerTrainingFilter filter = buildTrainerTrainingFilter();
        Training training = buildTraining();
        List<Training> trainings = List.of(training);
        TrainingResponseDTO expected = buildTrainingResponseDTO();

        when(dao.findByTrainerCriteria(filter)).thenReturn(trainings);
        when(mapper.toDto(training)).thenReturn(expected);

        List<TrainingResponseDTO> actual = service.getTrainerTrainings(filter);

        assertThat(actual)
                .hasSize(1)
                .contains(expected);
        verify(dao).findByTrainerCriteria(filter);
        verify(mapper).toDto(training);
    }

    private Training buildTraining() {
        return Training.builder()
                .trainingName(TRAINING_NAME)
                .trainingType(buildTrainingType())
                .trainingDate(LocalDate.of(2026, 4, 4))
                .trainingDuration(60)
                .trainee(buildTrainee())
                .trainer(buildTrainer())
                .build();
    }

    private Trainer buildTrainer() {
        User user = User.builder()
                .id(1L)
                .firstName("Callum")
                .lastName("Whitfield")
                .username("Callum.Whitfield")
                .password("pass111")
                .isActive(true)
                .build();

        return Trainer.builder()
                .id(1L)
                .user(user)
                .specialization(buildTrainingType())
                .build();
    }

    private Trainee buildTrainee() {
        User user = User.builder()
                .id(2L)
                .firstName("Nora")
                .lastName("Pemberton")
                .username("Nora.Pemberton")
                .password("pass222")
                .isActive(true)
                .build();

        return Trainee.builder()
                .id(1L)
                .user(user)
                .dateOfBirth(LocalDate.of(2000, 3, 10))
                .address("123 Main St")
                .build();
    }

    private TrainingType buildTrainingType() {
        return TrainingType.builder().trainingTypeName(TRAINING_TYPE_NAME).build();
    }

    private TrainingRequestDTO buildTrainingRequestDTO() {
        return TrainingRequestDTO.builder()
                .traineeUsername(TRAINEE_USERNAME)
                .trainerUsername(TRAINER_USERNAME)
                .trainingName(TRAINING_NAME)
                .trainingTypeName(TRAINING_TYPE_NAME)
                .trainingDate(LocalDate.of(2024, 1, 15))
                .trainingDuration(60)
                .build();
    }

    private TrainingResponseDTO buildTrainingResponseDTO() {
        return TrainingResponseDTO.builder()
                .id(VALID_ID)
                .traineeUsername(TRAINEE_USERNAME)
                .trainerUsername(TRAINER_USERNAME)
                .trainingName(TRAINING_NAME)
                .trainingTypeName(TRAINING_TYPE_NAME)
                .trainingDate(LocalDate.of(2024, 1, 15))
                .trainingDuration(60)
                .build();
    }

    private TraineeTrainingFilter buildTraineeTrainingFilter() {
        return TraineeTrainingFilter.builder()
                .username(TRAINEE_USERNAME)
                .fromDate(LocalDate.of(2024, 1, 1))
                .toDate(LocalDate.of(2024, 1, 30))
                .trainingTypeName(TRAINING_TYPE_NAME)
                .build();
    }

    private TrainerTrainingFilter buildTrainerTrainingFilter() {
        return TrainerTrainingFilter.builder()
                .username(TRAINER_USERNAME)
                .fromDate(LocalDate.of(2024, 1, 1))
                .toDate(LocalDate.of(2024, 1, 30))
                .build();
    }
}
