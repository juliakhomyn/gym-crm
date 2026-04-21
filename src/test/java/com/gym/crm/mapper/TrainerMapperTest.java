package com.gym.crm.mapper;

import com.gym.crm.dto.TrainerRequestDTO;
import com.gym.crm.dto.TrainerResponseDTO;
import com.gym.crm.dto.TrainerUpdateDTO;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.TrainingType;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TrainerMapperTest {
    private static final String FIRST_NAME = "Ellis";
    private static final String LAST_NAME = "Hargrove";
    private static final String USERNAME = "Ellis.Hargrove";
    private static final String PASSWORD = "encodedPassword";
    private static final String TRAINING_TYPE_NAME = "Yoga";

    private final TrainerMapper mapper = Mappers.getMapper(TrainerMapper.class);

    @Test
    void toEntity_shouldMapAllFields_whenMapFromTrainerRequestDTO() {
        TrainerRequestDTO trainerRequestDTO = buildTrainerRequestDTO();

        Trainer entity = mapper.toEntity(trainerRequestDTO);

        assertEquals(FIRST_NAME, entity.getFirstName());
        assertEquals(LAST_NAME, entity.getLastName());
        assertEquals(TRAINING_TYPE_NAME, entity.getSpecialization().getTrainingTypeName());
    }

    @Test
    void toEntity_shouldMapAllFields_whenMapFromTrainerUpdateDTO() {
        TrainerUpdateDTO trainerUpdateDTO = buildTrainerUpdateDTO();

        Trainer entity = mapper.toEntity(trainerUpdateDTO);

        assertEquals(USERNAME, entity.getUsername());
        assertEquals(PASSWORD, entity.getPassword());
        assertEquals(FIRST_NAME, entity.getFirstName());
        assertEquals(LAST_NAME, entity.getLastName());
        assertEquals(TRAINING_TYPE_NAME, entity.getSpecialization().getTrainingTypeName());
        assertEquals(true, entity.getIsActive());
    }

    @Test
    void toDto_shouldMapAllFields_whenMapFromTrainerEntity() {
        Trainer trainer = buildTrainer();

        TrainerResponseDTO responseDTO = mapper.toDto(trainer);

        assertEquals(USERNAME, responseDTO.getUsername());
        assertEquals(PASSWORD, responseDTO.getPassword());
        assertEquals(FIRST_NAME, responseDTO.getFirstName());
        assertEquals(LAST_NAME, responseDTO.getLastName());
        assertEquals(TRAINING_TYPE_NAME, responseDTO.getSpecialization());
        assertEquals(true, responseDTO.getIsActive());
    }

    @Test
    void mapStringToTrainingType_shouldReturnNull_whenInputIsNull() {
        assertNull(mapper.map((String) null));
    }

    @Test
    void mapTrainingTypeToString_shouldReturnNull_whenInputIsNull() {
        assertNull(mapper.map((TrainingType) null));
    }

    private TrainerRequestDTO buildTrainerRequestDTO() {
        return TrainerRequestDTO.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .specialization(TRAINING_TYPE_NAME)
                .build();
    }

    private TrainerUpdateDTO buildTrainerUpdateDTO() {
        return TrainerUpdateDTO.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .specialization(TRAINING_TYPE_NAME)
                .isActive(true)
                .build();
    }

    private Trainer buildTrainer() {
        return Trainer.builder()
                .userId(1L)
                .username(USERNAME)
                .password(PASSWORD)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .specialization(new TrainingType(TRAINING_TYPE_NAME))
                .isActive(true)
                .build();
    }
}
