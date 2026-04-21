package com.gym.crm.storage;

import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.enums.StorageNamespace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class InMemoryStorageTest {
    private InMemoryStorage inMemoryStorage;
    private Map<Long, Trainee> traineeStorage;
    private Map<Long, Trainer> trainerStorage;
    private Map<Long, Training> trainingStorage;

    @BeforeEach
    void setUp() {
        inMemoryStorage = new InMemoryStorage();
        traineeStorage = new HashMap<>();
        trainerStorage = new HashMap<>();
        trainingStorage = new HashMap<>();

        inMemoryStorage.setTraineeStorage(traineeStorage);
        inMemoryStorage.setTrainerStorage(trainerStorage);
        inMemoryStorage.setTrainingStorage(trainingStorage);
    }

    @Test
    void getStorage_shouldReturnTraineeStorage() {
        Map<Long, Trainee> actual = inMemoryStorage.getStorage(StorageNamespace.TRAINEE);

        assertNotNull(actual);
        assertSame(traineeStorage, actual);
    }

    @Test
    void getStorage_shouldReturnTrainerStorage() {
        Map<Long, Trainer> actual = inMemoryStorage.getStorage(StorageNamespace.TRAINER);

        assertNotNull(actual);
        assertSame(trainerStorage, actual);
    }

    @Test
    void getStorage_shouldReturnTrainingStorage() {
        Map<Long, Training> actual = inMemoryStorage.getStorage(StorageNamespace.TRAINING);

        assertNotNull(actual);
        assertSame(trainingStorage, actual);
    }
}
