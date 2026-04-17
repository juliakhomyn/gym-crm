package com.gym.crm.service.impl;

import com.gym.crm.dao.TrainerDAO;
import com.gym.crm.model.Trainer;
import com.gym.crm.service.TrainerService;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainerServiceImpl implements TrainerService {
    private static final String TRAINER_NOT_FOUND_BY_ID = "Trainer not found by id: %s";

    @Setter(onMethod_={@Autowired})
    private TrainerDAO trainerDAO;

    @Override
    public Trainer createTrainer(Trainer trainer) {
        return trainerDAO.save(trainer);
    }

    @Override
    public Trainer updateTrainer(Trainer trainer) {
        getTrainerById(trainer.getUserId());
        return trainerDAO.update(trainer);
    }

    @Override
    public Trainer getTrainerById(Long id) {
        return trainerDAO.findById(id).orElseThrow(() -> new IllegalArgumentException(String.format(TRAINER_NOT_FOUND_BY_ID, id)));
    }

    @Override
    public List<Trainer> getAllTrainers() {
        return trainerDAO.findAll();
    }
}
