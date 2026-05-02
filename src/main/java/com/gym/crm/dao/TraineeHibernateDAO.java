package com.gym.crm.dao;

import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;

import java.util.List;
import java.util.Optional;

public interface TraineeHibernateDAO {
    Trainee save(Trainee trainee);

    Trainee update(Trainee trainee);

    void delete(Long id);

    void deleteByUsername(String username);

    Optional<Trainee> findById(Long id);

    Optional<Trainee> findByUsername(String username);

    List<Trainee> findAll();

    Optional<Trainee> findByUsernameWithTrainers(String username);

    void updateTrainersList(String traineeUsername, List<Trainer> trainers);
}
