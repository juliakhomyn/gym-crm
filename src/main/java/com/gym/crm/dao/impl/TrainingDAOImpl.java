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
    private static final String INVALID_ID_EXCEPTION_MESSAGE = "ID must be positive and not null, got: %s";

    @Setter(onMethod_={@Autowired})
    private InMemoryStorage inMemoryStorage;

    @Override
    public Training save(Training training) {
        return trainingStorage().put(training.getId(), training);
    }

    @Override
    public Optional<Training> findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(String.format(INVALID_ID_EXCEPTION_MESSAGE, id));
        }
        return Optional.ofNullable(trainingStorage().get(id));
    }

    @Override
    public List<Training> findAll() {
        return trainingStorage().values().stream().toList();
    }

    private Map<Long, Training> trainingStorage() {
        return (Map<Long, Training>) inMemoryStorage.getStorage(StorageNamespace.TRAINING);
    }
}
