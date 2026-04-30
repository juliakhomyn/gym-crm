package com.gym.crm.dao;

import com.gym.crm.entity.Trainee;

import java.util.List;
import java.util.Optional;

public interface TraineeHibernateDAO {
    Trainee save(Trainee trainee);

    Trainee update(Trainee trainee);

    void delete(Long id);

    Optional<Trainee> findById(Long id);

    List<Trainee> findAll();
}
