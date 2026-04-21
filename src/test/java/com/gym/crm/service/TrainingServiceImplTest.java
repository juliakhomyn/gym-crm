package com.gym.crm.service;

import com.gym.crm.dao.TrainingDAO;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainingServiceImplTest {
    private static final long VALID_ID = 1L;
    private static final long NOT_FOUND_ID = 999L;

    @Mock
    private TrainingDAO dao;

    @InjectMocks
    private TrainingServiceImpl service;

    private Training training;
    private Training savedTraining;

    @BeforeEach
    void setUp() {
        training = buildTraining();

        savedTraining = training.toBuilder()
                .id(VALID_ID)
                .build();
    }

    @Test
    void createTraining_shouldSaveTrainingWithCredentials() {
        when(dao.save(any(Training.class))).thenReturn(savedTraining);

        Training result = service.createTraining(training);

        assertEquals(savedTraining, result);
        assertEquals(VALID_ID, result.getId());
        verify(dao).save(any(Training.class));
    }

    @Test
    void createTraining_shouldThrowException_whenTrainingIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.createTraining(null));

        assertEquals("Training cannot be null", exception.getMessage());
    }

    @Test
    void getTrainingById_shouldReturnTraining_whenTrainingExists() {
        when(dao.findById(VALID_ID)).thenReturn(Optional.of(savedTraining));

        Training result = service.getTrainingById(VALID_ID);

        assertEquals(savedTraining, result);
    }

    @Test
    void findById_shouldThrowException_whenTrainingNotFound() {
        when(dao.findById(NOT_FOUND_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> service.getTrainingById(NOT_FOUND_ID));
        assertEquals("Training not found by id: " + NOT_FOUND_ID, exception.getMessage());
    }

    @Test
    void getAllTrainings_shouldReturnAllTrainings_whenExist() {
        when(dao.findAll()).thenReturn(List.of(savedTraining));

        List<Training> result = service.getAllTrainings();

        assertEquals(1, result.size());
    }

    @Test
    void getAllTrainings_shouldReturnEmptyList_whenNoTrainings() {
        when(dao.findAll()).thenReturn(List.of());

        List<Training> trainings = service.getAllTrainings();

        assertTrue(trainings.isEmpty());
    }

    private Training buildTraining() {
        return Training.builder()
                .traineeId(VALID_ID)
                .trainerId(VALID_ID)
                .trainingName("Morning Yoga")
                .trainingType(new TrainingType("Yoga"))
                .trainingDate(LocalDate.of(2026, 4, 4))
                .trainingDuration(60)
                .build();
    }
}
