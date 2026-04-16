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
    private static final String TRAINEE_NOT_FOUND_BY_ID = "Trainee not found by id: %s";

    @Setter(onMethod_={@Autowired})
    private InMemoryStorage inMemoryStorage;

    @Override
    public Trainee save(Trainee trainee) {
        return traineeStorage().put(trainee.getUserId(), trainee);
    }

    @Override
    public Trainee update(Trainee trainee) {
        if (!traineeStorage().containsKey(trainee.getUserId())) {
            throw new IllegalArgumentException(String.format(TRAINEE_NOT_FOUND_BY_ID, trainee.getUserId()));
        }
        return traineeStorage().put(trainee.getUserId(), trainee);
    }

    @Override
    public void delete(Long id) {
        if (!traineeStorage().containsKey(id)) {
            throw new IllegalArgumentException(String.format(TRAINEE_NOT_FOUND_BY_ID, id));
        }
        traineeStorage().remove(id);
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        Optional<Trainee> trainee = Optional.ofNullable(traineeStorage().get(id));
        if (trainee.isEmpty()) {
            throw new IllegalArgumentException(String.format(TRAINEE_NOT_FOUND_BY_ID, id));
        }
        return trainee;
    }

    @Override
    public List<Trainee> findAll() {
        return traineeStorage().values().stream().toList();
    }

    private Map<Long, Trainee> traineeStorage() {
        return (Map<Long, Trainee>) inMemoryStorage.getStorage(StorageNamespace.TRAINEE);
    }
}
