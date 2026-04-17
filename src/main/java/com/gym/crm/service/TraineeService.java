package com.gym.crm.service;

import com.gym.crm.model.Trainee;

import java.util.List;

public interface TraineeService {
    Trainee createTrainee(Trainee trainee);

    Trainee updateTrainee(Trainee trainee);

    void deleteTrainee(Long id);

    Trainee getTraineeById(Long id);

    List<Trainee> getAllTrainees();
}
