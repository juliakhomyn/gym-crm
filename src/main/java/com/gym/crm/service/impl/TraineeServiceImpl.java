package com.gym.crm.service.impl;

import com.gym.crm.dao.TraineeDAO;
import com.gym.crm.model.Trainee;
import com.gym.crm.service.TraineeService;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TraineeServiceImpl implements TraineeService {
    private static final String TRAINEE_NOT_FOUND_BY_ID = "Trainee not found by id: %s";

    @Setter(onMethod_={@Autowired})
    private TraineeDAO traineeDAO;

    @Override
    public Trainee createTrainee(Trainee trainee) {
        return traineeDAO.save(trainee);
    }

    @Override
    public Trainee updateTrainee(Trainee trainee) {
        getTraineeById(trainee.getUserId());
        return traineeDAO.update(trainee);
    }

    @Override
    public void deleteTrainee(Long id) {
        getTraineeById(id);
        traineeDAO.delete(id);
    }

    @Override
    public Trainee getTraineeById(Long id) {
        return traineeDAO.findById(id).orElseThrow(() -> new IllegalArgumentException(String.format(TRAINEE_NOT_FOUND_BY_ID, id)));
    }

    @Override
    public List<Trainee> getAllTrainees() {
        return traineeDAO.findAll();
    }
}
