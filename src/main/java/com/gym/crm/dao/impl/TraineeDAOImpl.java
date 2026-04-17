package com.gym.crm.dao.impl;

import com.gym.crm.dao.TraineeDAO;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.enums.StorageNamespace;
import com.gym.crm.storage.InMemoryStorage;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TraineeDAOImpl implements TraineeDAO {
    private static final String INVALID_ID_EXCEPTION_MESSAGE = "ID must be positive and not null, got: %s";

    @Setter(onMethod_={@Autowired})
    private InMemoryStorage inMemoryStorage;

    @Override
    public Trainee save(Trainee trainee) {
        return traineeStorage().put(trainee.getUserId(), trainee);
    }

    @Override
    public Trainee update(Trainee trainee) {
        if (trainee.getUserId() == null || trainee.getUserId() <= 0) {
            throw new IllegalArgumentException(String.format(INVALID_ID_EXCEPTION_MESSAGE, trainee.getUserId()));
        }
        return traineeStorage().put(trainee.getUserId(), trainee);
    }

    @Override
    public void delete(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(String.format(INVALID_ID_EXCEPTION_MESSAGE, id));
        }
        traineeStorage().remove(id);
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(String.format(INVALID_ID_EXCEPTION_MESSAGE, id));
        }
        return Optional.ofNullable(traineeStorage().get(id));
    }

    @Override
    public List<Trainee> findAll() {
        return traineeStorage().values().stream().toList();
    }

    private Map<Long, Trainee> traineeStorage() {
        return (Map<Long, Trainee>) inMemoryStorage.getStorage(StorageNamespace.TRAINEE);
    }
}
