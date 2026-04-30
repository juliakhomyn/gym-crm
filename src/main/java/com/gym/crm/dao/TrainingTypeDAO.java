package com.gym.crm.dao;

import com.gym.crm.entity.TrainingType;

import java.util.List;
import java.util.Optional;

public interface TrainingTypeDAO {
    Optional<TrainingType> findById(Long id);

    Optional<TrainingType> findByTrainingTypeName(String name);

    List<TrainingType> findAll();
}
