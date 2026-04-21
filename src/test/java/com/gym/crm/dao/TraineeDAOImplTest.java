package com.gym.crm.dao;

import com.gym.crm.dao.impl.TraineeDAOImpl;
import com.gym.crm.model.Trainee;
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

public class TraineeDAOImplTest {
    private static final String INVALID_ID_MESSAGE = "ID must be positive and not null, got: %s";

    private TraineeDAOImpl dao;
    private Map<Long, Trainee> map;

    @BeforeEach
    void setUp() {
        InMemoryStorage inMemoryStorage = mock(InMemoryStorage.class);

        map = new HashMap<>();
        when(inMemoryStorage.getStorage(StorageNamespace.TRAINEE)).thenReturn((Map) map);

        dao = new TraineeDAOImpl();
        dao.setInMemoryStorage(inMemoryStorage);
    }

    @Test
    void save_shouldSaveTraineeAndGenerateId_whenIdIsNull() {
        Trainee trainee = buildTrainee();

        Trainee actual = dao.save(trainee);

        assertNotNull(actual.getUserId());
        assertEquals(1L, actual.getUserId());
        assertTrue(map.containsKey(actual.getUserId()));
    }

    @Test
    void save_shouldSaveTrainee_whenIdIsNotNull() {
        Trainee trainee = buildTrainee().toBuilder().userId(4L).build();

        Trainee actual = dao.save(trainee);

        assertNotNull(actual.getUserId());
        assertEquals(4L, actual.getUserId());
        assertTrue(map.containsKey(actual.getUserId()));
    }

    @Test
    void save_shouldThrowException_whenSavingNullTrainee() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> dao.save(null));

        assertEquals("Trainee cannot be null", exception.getMessage());
    }

    @Test
    void update_shouldUpdateExistingTrainee_whenExists() {
        Trainee trainee = buildTrainee();
        Trainee saved = dao.save(trainee);
        Trainee updated = saved.toBuilder().address("new address").build();

        Trainee actual = dao.update(updated);

        assertEquals("new address", actual.getAddress());
        assertTrue(map.containsKey(actual.getUserId()));
    }

    @Test
    void update_shouldThrowException_whenIdIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> dao.update(buildTrainee()));

        assertEquals(String.format(INVALID_ID_MESSAGE, "null"), exception.getMessage());
    }

    @Test
    void delete_shouldDeleteTrainee_whenExists() {
        Trainee trainee = buildTrainee();
        dao.save(trainee);

        dao.delete(1L);

        assertTrue(dao.findAll().isEmpty());
    }

    @Test
    void delete_shouldThrowException_whenIdIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> dao.delete(null));

        assertEquals(String.format(INVALID_ID_MESSAGE, "null"), exception.getMessage());
    }

    @Test
    void findById_shouldReturnTrainee_whenExists() {
        Trainee trainee = buildTrainee();
        Trainee saved = dao.save(trainee);

        Optional<Trainee> actual = dao.findById(saved.getUserId());

        assertTrue(actual.isPresent());
        assertEquals(saved.getUserId(), actual.get().getUserId());
    }

    @Test
    void findById_shouldReturnEmptyOptional_whenNotFound() {
        Optional<Trainee> actual = dao.findById(999L);

        assertTrue(actual.isEmpty());
    }

    @Test
    void findById_shouldThrowException_whenIdIsZero() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> dao.findById(0L));

        assertEquals(String.format(INVALID_ID_MESSAGE, "0"), exception.getMessage());
    }

    @Test
    void findAll_shouldReturnAllTrainees_whenExist() {
        dao.save(buildTrainee());
        dao.save(buildTrainee());

        List<Trainee> actual = dao.findAll();

        assertEquals(2, actual.size());
    }

    @Test
    void findAll_shouldReturnEmptyList_whenNoTrainees() {
        List<Trainee> actual = dao.findAll();

        assertTrue(actual.isEmpty());
    }

    private Trainee buildTrainee() {
        return Trainee.builder()
                .firstName("Callum")
                .lastName("Whitfield")
                .username("Callum.Whitfield")
                .password("password")
                .isActive(true)
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("123 Main St")
                .build();
    }
}
