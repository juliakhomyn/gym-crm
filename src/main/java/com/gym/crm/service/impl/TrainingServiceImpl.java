package com.gym.crm.service.impl;

import com.gym.crm.dao.TrainingDAO;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.model.Training;
import com.gym.crm.service.TrainingService;
import com.gym.crm.util.Validator;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainingServiceImpl implements TrainingService {
    private static final String TRAINING_NOT_FOUND_BY_ID = "Training not found by id: %s";
    private static final String TRAINING = "Training";

    @Setter(onMethod_={@Autowired})
    private TrainingDAO trainingDAO;

    @Override
    public Training createTraining(Training training) {
        Validator.validateNotNull(training, TRAINING);

        return trainingDAO.save(training);
    }

    @Override
    public Training getTrainingById(Long id) {
        return trainingDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(TRAINING_NOT_FOUND_BY_ID, id)));
    }

    @Override
    public List<Training> getAllTrainings() {
        return trainingDAO.findAll();
    }
}
