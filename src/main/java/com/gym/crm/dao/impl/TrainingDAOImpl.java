package com.gym.crm.dao.impl;

import com.gym.crm.dao.TrainingDAO;
import com.gym.crm.model.Training;
import com.gym.crm.model.enums.StorageNamespace;
import com.gym.crm.storage.InMemoryStorage;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TrainingDAOImpl implements TrainingDAO {
    private static final String TRAINING_NOT_FOUND_BY_ID = "Training not found by is: %s";

    @Setter(onMethod_={@Autowired})
    private InMemoryStorage inMemoryStorage;

    @Override
    public Training save(Training training) {
        return trainingStorage().put(training.getId(), training);
    }

    @Override
    public Optional<Training> findById(Long id) {
        Optional<Training> training = Optional.ofNullable(trainingStorage().get(id));
        if (training.isEmpty()) {
            throw new IllegalArgumentException(String.format(TRAINING_NOT_FOUND_BY_ID, id));
        }
        return training;
    }

    @Override
    public List<Training> findAll() {
        return trainingStorage().values().stream().toList();
    }

    private Map<Long, Training> trainingStorage() {
        return (Map<Long, Training>) inMemoryStorage.getStorage(StorageNamespace.TRAINING);
    }
}
