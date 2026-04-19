package com.gym.crm.dao.impl;

import com.gym.crm.dao.TrainingDAO;
import com.gym.crm.model.Training;
import com.gym.crm.model.enums.StorageNamespace;
import com.gym.crm.storage.InMemoryStorage;
import com.gym.crm.util.Validator;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TrainingDAOImpl implements TrainingDAO {

    @Setter(onMethod_={@Autowired})
    private InMemoryStorage inMemoryStorage;

    @Override
    public Training save(Training training) {
        Training toSave = training.getId() == null
                ? training.toBuilder().id(generateId()).build()
                : training;
        trainingStorage().put(toSave.getId(), toSave);
        return toSave;
    }

    @Override
    public Optional<Training> findById(Long id) {
        Validator.validateId(id);
        return Optional.ofNullable(trainingStorage().get(id));
    }

    @Override
    public List<Training> findAll() {
        return trainingStorage().values().stream().toList();
    }

    private Map<Long, Training> trainingStorage() {
        return (Map<Long, Training>) inMemoryStorage.getStorage(StorageNamespace.TRAINING);
    }

    private long generateId() {
        return trainingStorage().keySet().stream().max(Long::compareTo).orElse(0L) + 1;
    }
}
