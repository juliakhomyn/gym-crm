package com.gym.crm.dao;

import com.gym.crm.filter.TraineeTrainingFilter;
import com.gym.crm.filter.TrainerTrainingFilter;
import com.gym.crm.entity.Training;

import java.util.List;
import java.util.Optional;

public interface TrainingHibernateDAO {
    Training save(Training training);

    Optional<Training> findById(Long id);

    List<Training> findAll();

    List<Training> findByTraineeCriteria(TraineeTrainingFilter filter);

    List<Training> findByTrainerCriteria(TrainerTrainingFilter filter);
}
