package com.gym.crm.storage;

import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.enums.StorageNamespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryStorage {

    private final Map<StorageNamespace, Map<Long, ?>> storage = new HashMap<>();

    @Autowired
    public void setTraineeStorage(Map<Long, Trainee> traineeStorage) {
        storage.put(StorageNamespace.TRAINEE, traineeStorage);
    }

    @Autowired
    public void setTrainerStorage(Map<Long, Trainer> trainerStorage) {
        storage.put(StorageNamespace.TRAINER, trainerStorage);
    }

    @Autowired
    public void setTrainingStorage(Map<Long, Training> trainingStorage) {
        storage.put(StorageNamespace.TRAINING, trainingStorage);
    }

    public Map<Long, ?> getStorage(StorageNamespace namespace) {
        return storage.get(namespace);
    }
}
