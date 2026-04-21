package com.gym.crm.dao;

import com.gym.crm.dao.impl.TrainerDAOImpl;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.TrainingType;
import com.gym.crm.model.enums.StorageNamespace;
import com.gym.crm.storage.InMemoryStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

public class TrainerDAOImplTest {
    private static final String INVALID_ID_MESSAGE = "ID must be positive and not null, got: %s";

    private TrainerDAOImpl dao;
    private Map<Long, Trainer> map;

    @BeforeEach
    void setUp() {
        InMemoryStorage inMemoryStorage = mock(InMemoryStorage.class);

        map = new HashMap<>();
        when(inMemoryStorage.getStorage(StorageNamespace.TRAINER)).thenReturn((Map) map);

        dao = new TrainerDAOImpl();
        dao.setInMemoryStorage(inMemoryStorage);
    }

    @Test
    void save_shouldSaveTrainerAndGenerateId_whenIdIsNull() {
        Trainer trainer = buildTrainer();

        Trainer actual = dao.save(trainer);

        assertNotNull(actual.getUserId());
        assertEquals(1L, actual.getUserId());
        assertTrue(map.containsKey(actual.getUserId()));
    }

    @Test
    void save_shouldSaveTrainer_whenIdIsNotNull() {
        Trainer trainer = buildTrainer().toBuilder().userId(4L).build();

        Trainer actual = dao.save(trainer);

        assertNotNull(actual.getUserId());
        assertEquals(4L, actual.getUserId());
        assertTrue(map.containsKey(actual.getUserId()));
    }

    @Test
    void save_shouldThrowException_whenSavingNullTrainer() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> dao.save(null));

        assertEquals("Trainer cannot be null", exception.getMessage());
    }

    @Test
    void update_shouldUpdateExistingTrainer_whenExists() {
        Trainer trainer = buildTrainer();
        Trainer saved = dao.save(trainer);
        Trainer updated = saved.toBuilder().specialization(new TrainingType("Pilates")).build();

        Trainer actual = dao.update(updated);

        assertEquals("Pilates", actual.getSpecialization().getTrainingTypeName());
        assertTrue(map.containsKey(actual.getUserId()));
    }

    @Test
    void update_shouldThrowException_whenIdIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> dao.update(buildTrainer()));

        assertEquals(String.format(INVALID_ID_MESSAGE, "null"), exception.getMessage());
    }

    @Test
    void findById_shouldReturnTrainer_whenExists() {
        Trainer trainer = buildTrainer();
        Trainer saved = dao.save(trainer);

        Optional<Trainer> actual = dao.findById(saved.getUserId());

        assertTrue(actual.isPresent());
        assertEquals(saved.getUserId(), actual.get().getUserId());
    }

    @Test
    void findById_shouldReturnEmptyOptional_whenNotFound() {
        Optional<Trainer> actual = dao.findById(999L);

        assertTrue(actual.isEmpty());
    }

    @Test
    void findById_shouldThrowException_whenIdIsZero() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> dao.findById(0L));

        assertEquals(String.format(INVALID_ID_MESSAGE, "0"), exception.getMessage());
    }

    @Test
    void findAll_shouldReturnAllTrainers_whenExist() {
        dao.save(buildTrainer());
        dao.save(buildTrainer());

        List<Trainer> actual = dao.findAll();

        assertEquals(2, actual.size());
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoTrainers() {
        List<Trainer> actual = dao.findAll();

        assertTrue(actual.isEmpty());
    }

    private Trainer buildTrainer() {
        return Trainer.builder()
                .firstName("Callum")
                .lastName("Whitfield")
                .username("Callum.Whitfield")
                .password("password")
                .specialization(new TrainingType("Yoga"))
                .isActive(true)
                .build();
    }
}
