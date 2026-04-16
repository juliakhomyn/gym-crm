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
    private static final String TRAINER_NOT_FOUND_BY_ID = "Trainer not found by id: %s";

    @Setter(onMethod_={@Autowired})
    private InMemoryStorage inMemoryStorage;

    @Override
    public Trainer save(Trainer trainer) {
        return trainerStorage().put(trainer.getUserId(), trainer);
    }

    @Override
    public Trainer update(Trainer trainer) {
        if (!trainerStorage().containsKey(trainer.getUserId())) {
            throw new IllegalArgumentException(String.format(TRAINER_NOT_FOUND_BY_ID, trainer.getUserId()));
        }
        return trainerStorage().put(trainer.getUserId(), trainer);
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        Optional<Trainer> trainer = Optional.ofNullable(trainerStorage().get(id));
        if (trainer.isEmpty()) {
            throw new IllegalArgumentException(String.format(TRAINER_NOT_FOUND_BY_ID, id));
        }
        return trainer;
    }

    @Override
    public List<Trainer> findAll() {
        return trainerStorage().values().stream().toList();
    }

    private Map<Long, Trainer> trainerStorage() {
        return (Map<Long, Trainer>) inMemoryStorage.getStorage(StorageNamespace.TRAINER);
    }
}
