package com.gym.crm.dao.impl;

import com.gym.crm.dao.TrainerDAO;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.enums.StorageNamespace;
import com.gym.crm.storage.InMemoryStorage;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TrainerDAOImpl implements TrainerDAO {
    private static final String INVALID_ID_EXCEPTION_MESSAGE = "ID must be positive and not null, got: %s";

    @Setter(onMethod_={@Autowired})
    private InMemoryStorage inMemoryStorage;

    @Override
    public Trainer save(Trainer trainer) {
        return trainerStorage().put(trainer.getUserId(), trainer);
    }

    @Override
    public Trainer update(Trainer trainer) {
        if (trainer.getUserId() == null || trainer.getUserId() <= 0) {
            throw new IllegalArgumentException(String.format(INVALID_ID_EXCEPTION_MESSAGE, trainer.getUserId()));
        }
        return trainerStorage().put(trainer.getUserId(), trainer);
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(String.format(INVALID_ID_EXCEPTION_MESSAGE, id));
        }
        return Optional.ofNullable(trainerStorage().get(id));
    }

    @Override
    public List<Trainer> findAll() {
        return trainerStorage().values().stream().toList();
    }

    private Map<Long, Trainer> trainerStorage() {
        return (Map<Long, Trainer>) inMemoryStorage.getStorage(StorageNamespace.TRAINER);
    }
}
