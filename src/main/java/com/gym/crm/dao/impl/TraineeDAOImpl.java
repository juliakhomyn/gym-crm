package com.gym.crm.dao.impl;

import com.gym.crm.dao.TraineeDAO;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.enums.StorageNamespace;
import com.gym.crm.storage.InMemoryStorage;
import com.gym.crm.util.Validator;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Repository
public class TraineeDAOImpl implements TraineeDAO {

    @Setter(onMethod_={@Autowired})
    private InMemoryStorage inMemoryStorage;

    @Override
    public Trainee save(Trainee trainee) {
        Validator.validateNotNull(trainee, "Trainee");

        Trainee toSave = Objects.isNull(trainee.getUserId())
                ? trainee.toBuilder().userId(generateId()).build()
                : trainee;
        traineeStorage().put(toSave.getUserId(), toSave);

        return toSave;
    }

    @Override
    public Trainee update(Trainee trainee) {
        Validator.validateId(trainee.getUserId());

        traineeStorage().put(trainee.getUserId(), trainee);

        return trainee;
    }

    @Override
    public void delete(Long id) {
        Validator.validateId(id);

        traineeStorage().remove(id);
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        Validator.validateId(id);

        return Optional.ofNullable(traineeStorage().get(id));
    }

    @Override
    public List<Trainee> findAll() {
        return traineeStorage().values().stream().toList();
    }

    private Map<Long, Trainee> traineeStorage() {
        return inMemoryStorage.getStorage(StorageNamespace.TRAINEE);
    }

    private long generateId() {
        return traineeStorage().keySet().stream().max(Long::compareTo).orElse(0L) + 1;
    }
}
