package com.gym.crm.mapper;

import com.gym.crm.dto.training.TrainingRequestDTO;
import com.gym.crm.dto.training.TrainingResponseDTO;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import com.gym.crm.model.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrainingMapperTest {
    private static final String TRAINEE_USERNAME = "Trainee.Username";
    private static final String TRAINER_USERNAME = "Trainer.Username";
    private static final String TRAINING_NAME = "Morning Cardio";
    private static final String TRAINING_TYPE_NAME = "Cardio";
    private static final LocalDate TRAINING_DATE = LocalDate.of(2026, 4, 4);
    private static final int TRAINING_DURATION = 60;
    private static final long VALID_ID = 1L;

    private final TrainingMapper mapper = Mappers.getMapper(TrainingMapper.class);

    @Test
    void toEntity_shouldMapAllFields_whenMapFromTrainingRequestDTO() {
        TrainingRequestDTO trainingRequestDTO = buildTrainingRequestDTO();

        Training training = mapper.toEntity(trainingRequestDTO);

        assertEquals(TRAINING_NAME, training.getTrainingName());
        assertEquals(TRAINING_TYPE_NAME, training.getTrainingType().getTrainingTypeName());
        assertEquals(TRAINING_DATE, training.getTrainingDate());
        assertEquals(TRAINING_DURATION, training.getTrainingDuration());
    }

    @Test
    void toDto_shouldMapAllFields_whenMapFromTrainingEntity() {
        Training training = buildTraining();

        TrainingResponseDTO trainingResponseDTO = mapper.toDto(training);

        assertEquals(TRAINEE_USERNAME, trainingResponseDTO.getTraineeUsername());
        assertEquals(TRAINER_USERNAME, trainingResponseDTO.getTrainerUsername());
        assertEquals(TRAINING_NAME, trainingResponseDTO.getTrainingName());
        assertEquals(TRAINING_TYPE_NAME, trainingResponseDTO.getTrainingTypeName());
        assertEquals(TRAINING_DATE, trainingResponseDTO.getTrainingDate());
        assertEquals(TRAINING_DURATION, trainingResponseDTO.getTrainingDuration());
    }
    
    private TrainingRequestDTO buildTrainingRequestDTO() {
        return TrainingRequestDTO.builder()
                .traineeUsername(TRAINEE_USERNAME)
                .trainerUsername(TRAINER_USERNAME)
                .trainingName(TRAINING_NAME)
                .trainingTypeName(TRAINING_TYPE_NAME)
                .trainingDate(TRAINING_DATE)
                .trainingDuration(TRAINING_DURATION)
                .build();
    }
    
    private Training buildTraining() {
        User traineeUser = User.builder().username(TRAINEE_USERNAME).build();
        User trainerUser = User.builder().username(TRAINER_USERNAME).build();

        Trainee trainee = Trainee.builder().id(VALID_ID).user(traineeUser).build();
        Trainer trainer = Trainer.builder().id(VALID_ID).user(trainerUser).build();

        return Training.builder()
                .id(VALID_ID)
                .trainingName(TRAINING_NAME)
                .trainingType(buildTrainingType())
                .trainingDate(TRAINING_DATE)
                .trainingDuration(TRAINING_DURATION)
                .trainee(trainee)
                .trainer(trainer)
                .build();
    }

    private TrainingType buildTrainingType() {
        return TrainingType.builder()
                .id(1L)
                .trainingTypeName(TRAINING_TYPE_NAME)
                .build();
    }
}
