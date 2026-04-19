package com.gym.crm.dao.impl;

import com.gym.crm.dao.TrainerDAO;
import com.gym.crm.model.Trainer;
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
public class TrainerDAOImpl implements TrainerDAO {

    @Setter(onMethod_={@Autowired})
    private InMemoryStorage inMemoryStorage;

    @Override
    public Trainer save(Trainer trainer) {
        Trainer toSave = trainer.getUserId() == null
                ? trainer.toBuilder().userId(generateId()).build()
                : trainer;
        trainerStorage().put(toSave.getUserId(), toSave);
        return toSave;
    }

    @Override
    public Trainer update(Trainer trainer) {
        Validator.validateId(trainer.getUserId());
        trainerStorage().put(trainer.getUserId(), trainer);
        return trainer;
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        Validator.validateId(id);
        return Optional.ofNullable(trainerStorage().get(id));
    }

    @Override
    public List<Trainer> findAll() {
        return trainerStorage().values().stream().toList();
    }

    private Map<Long, Trainer> trainerStorage() {
        return (Map<Long, Trainer>) inMemoryStorage.getStorage(StorageNamespace.TRAINER);
    }

    private long generateId() {
        return trainerStorage().keySet().stream().max(Long::compareTo).orElse(0L) + 1;
    }
}
