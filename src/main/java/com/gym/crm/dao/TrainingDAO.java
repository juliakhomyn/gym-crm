package com.gym.crm.dao;

import com.gym.crm.search.filter.TraineeTrainingFilter;
import com.gym.crm.search.filter.TrainerTrainingFilter;
import com.gym.crm.model.Training;

import java.util.List;
import java.util.Optional;

public interface TrainingDAO {
    Training save(Training training);

    Optional<Training> findById(Long id);

    List<Training> findAll();

    List<Training> findByTraineeCriteria(TraineeTrainingFilter filter);

    List<Training> findByTrainerCriteria(TrainerTrainingFilter filter);
}
