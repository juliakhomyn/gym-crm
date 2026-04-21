package com.gym.crm.dao;

import com.gym.crm.dao.impl.TrainingDAOImpl;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import com.gym.crm.model.enums.StorageNamespace;
import com.gym.crm.storage.InMemoryStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TrainingDAOImplTest {

    private TrainingDAOImpl dao;
    private Map<Long, Training> map;

    @BeforeEach
    void setUp() {
        InMemoryStorage inMemoryStorage = mock(InMemoryStorage.class);

        map = new HashMap<>();
        when(inMemoryStorage.getStorage(StorageNamespace.TRAINING)).thenReturn((Map) map);

        dao = new TrainingDAOImpl();
        dao.setInMemoryStorage(inMemoryStorage);
    }

    @Test
    void save_shouldSaveTrainingAndGenerateId_whenIdIsNull() {
        Training training = buildTraining();

        Training actual = dao.save(training);

        assertNotNull(actual.getId());
        assertEquals(1L, actual.getId());
        assertTrue(map.containsKey(actual.getId()));
    }

    @Test
    void save_shouldSaveTraining_whenIdIsNotNull() {
        Training training = buildTraining().toBuilder().id(4L).build();

        Training actual = dao.save(training);

        assertNotNull(actual.getId());
        assertEquals(4L, actual.getId());
        assertTrue(map.containsKey(actual.getId()));
    }

    @Test
    void save_shouldThrowException_whenSavingNullTrainer() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> dao.save(null));

        assertEquals("Training cannot be null", exception.getMessage());
    }

    @Test
    void findById_shouldReturnTraining_whenExists() {
        Training training = buildTraining();
        Training saved = dao.save(training);

        Optional<Training> actual = dao.findById(saved.getId());

        assertTrue(actual.isPresent());
        assertEquals(saved.getId(), actual.get().getId());
    }

    @Test
    void findById_shouldReturnEmptyOptional_whenNotFound() {
        Optional<Training> actual = dao.findById(999L);

        assertTrue(actual.isEmpty());
    }

    @Test
    void findById_shouldThrowException_whenIdIsZero() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> dao.findById(0L));

        assertEquals("ID must be positive and not null, got: 0", exception.getMessage());
    }

    @Test
    void findAll_shouldReturnAllTrainings_whenExist() {
        dao.save(buildTraining());
        dao.save(buildTraining());

        List<Training> actual = dao.findAll();

        assertEquals(2, actual.size());
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoTrainings() {
        List<Training> actual = dao.findAll();

        assertTrue(actual.isEmpty());
    }

    private Training buildTraining() {
        return Training.builder()
                .traineeId(1L)
                .trainerId(1L)
                .trainingName("Morning Yoga")
                .trainingType(new TrainingType("Yoga"))
                .trainingDate(LocalDate.of(2026, 4, 4))
                .trainingDuration(60)
                .build();
    }
}
