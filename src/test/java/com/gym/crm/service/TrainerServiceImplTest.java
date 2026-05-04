package com.gym.crm.service;

import com.gym.crm.dao.TrainerDAO;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.TrainingType;
import com.gym.crm.model.User;
import com.gym.crm.service.impl.TrainerServiceImpl;
import com.gym.crm.util.UserCredentialGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainerServiceImplTest {
    private static final String FIRST_NAME = "Owen";
    private static final String LAST_NAME = "Castleberry";
    private static final String USERNAME = "Owen.Castleberry";
    private static final String ENCODED_PASSWORD = "encodedPassword";
    private static final String RAW_PASSWORD = "rawPassword";
    private static final long VALID_ID = 1L;
    private static final long NOT_FOUND_ID = 999L;

    private static final String TRAINER_CANNOT_BE_NULL = "Trainer cannot be null";
    private static final String TRAINER_NOT_FOUND_BY_ID = "Trainer not found by id: %s";

    @Mock
    private TrainerDAO dao;
    @Mock
    private UserCredentialGenerator userCredentialGenerator;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TrainerServiceImpl service;

    private Trainer trainer;
    private Trainer savedTrainer;

    @BeforeEach
    void setUp() {
        trainer = buildTrainer();

        savedTrainer = trainer.toBuilder()
                .id(VALID_ID)
                .user(buildSavedUser())
                .build();
    }

    @Test
    void createTrainer_shouldSaveTrainerWithCredentials() {
        when(userCredentialGenerator.generateUsername(FIRST_NAME, LAST_NAME)).thenReturn(USERNAME);
        when(userCredentialGenerator.generatePassword()).thenReturn(RAW_PASSWORD);
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(dao.save(any(Trainer.class))).thenReturn(savedTrainer);

        Trainer result = service.createTrainer(trainer);

        assertEquals(USERNAME, result.getUser().getUsername());
        assertEquals(ENCODED_PASSWORD, result.getUser().getPassword());
        assertTrue(result.getUser().getIsActive());
        verify(dao).save(any(Trainer.class));
    }

    @Test
    void createTrainer_shouldThrowException_whenTrainerIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.createTrainer(null));

        assertEquals(TRAINER_CANNOT_BE_NULL, exception.getMessage());
    }

    @Test
    void updateTrainer_shouldUpdateTrainer_whenTrainerExists() {
        Trainer expected = savedTrainer.toBuilder()
                .specialization(buildTrainingType())
                .build();

        when(dao.findById(VALID_ID)).thenReturn(Optional.of(savedTrainer));
        when(dao.update(savedTrainer)).thenReturn(expected);

        Trainer actual = service.updateTrainer(savedTrainer);

        assertEquals(expected, actual);
        verify(dao).update(savedTrainer);
    }

    @Test
    void updateTrainer_shouldThrowException_whenTrainerIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.updateTrainer(null));

        assertEquals(TRAINER_CANNOT_BE_NULL, exception.getMessage());
    }

    @Test
    void updateTrainer_shouldThrowException_whenTrainerNotFound() {
        when(dao.findById(NOT_FOUND_ID)).thenReturn(Optional.empty());
        Trainer nonExistent = buildNonExistentTrainer();

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> service.updateTrainer(nonExistent));

        assertEquals(String.format(TRAINER_NOT_FOUND_BY_ID, NOT_FOUND_ID), exception.getMessage());
        verify(dao, never()).update(any(Trainer.class));
    }

    @Test
    void getTrainerById_shouldReturnTrainer_whenTrainerExists() {
        when(dao.findById(VALID_ID)).thenReturn(Optional.of(savedTrainer));

        Trainer result = service.getTrainerById(VALID_ID);

        assertEquals(savedTrainer, result);
    }

    @Test
    void findById_shouldThrowException_whenTrainerNotFound() {
        when(dao.findById(NOT_FOUND_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> service.getTrainerById(NOT_FOUND_ID));
        assertEquals(String.format(TRAINER_NOT_FOUND_BY_ID, NOT_FOUND_ID), exception.getMessage());
    }

    @Test
    void getAllTrainers_shouldReturnAllTrainers_whenExist() {
        when(dao.findAll()).thenReturn(List.of(savedTrainer));

        List<Trainer> result = service.getAllTrainers();

        assertEquals(1, result.size());
    }

    @Test
    void getAllTrainers_shouldReturnEmptyList_whenNoTrainers() {
        when(dao.findAll()).thenReturn(List.of());

        List<Trainer> trainers = service.getAllTrainers();

        assertTrue(trainers.isEmpty());
    }

    private Trainer buildTrainer() {
        return Trainer.builder()
                .user(buildUser())
                .specialization(buildTrainingType())
                .build();
    }

    private User buildUser() {
        return User.builder()
                .id(VALID_ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .username(USERNAME)
                .password(ENCODED_PASSWORD)
                .isActive(true)
                .build();
    }

    private User buildSavedUser() {
        return User.builder()
                .id(VALID_ID)
                .username(USERNAME)
                .password(ENCODED_PASSWORD)
                .isActive(true)
                .build();
    }

    private TrainingType buildTrainingType() {
        return TrainingType.builder().trainingTypeName("new type").build();
    }

    private Trainer buildNonExistentTrainer() {
        User user = User.builder()
                .id(NOT_FOUND_ID)
                .build();

        return savedTrainer.toBuilder()
                .id(NOT_FOUND_ID)
                .user(user)
                .build();
    }
}
