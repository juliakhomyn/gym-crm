package com.gym.crm.service.impl;

import com.gym.crm.dao.TrainerDAO;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.model.Trainer;
import com.gym.crm.service.TrainerService;
import com.gym.crm.util.UserCredentialGenerator;
import com.gym.crm.util.Validator;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainerServiceImpl implements TrainerService {
    private static final String TRAINER_NOT_FOUND_BY_ID = "Trainer not found by id: %s";
    private static final String TRAINER = "Trainer";

    @Setter(onMethod_={@Autowired})
    private TrainerDAO trainerDAO;

    @Setter(onMethod_={@Autowired})
    private UserCredentialGenerator userCredentialGenerator;

    @Setter(onMethod_={@Autowired})
    private PasswordEncoder passwordEncoder;

    @Override
    public Trainer createTrainer(Trainer trainer) {
        Validator.validateNotNull(trainer, TRAINER);

        String username = userCredentialGenerator.generateUsername(trainer.getFirstName(), trainer.getLastName());
        String rawPassword = userCredentialGenerator.generatePassword();

        Trainer withCredentials = trainer.toBuilder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .isActive(true)
                .build();
        return trainerDAO.save(withCredentials);
    }

    @Override
    public Trainer updateTrainer(Trainer trainer) {
        Validator.validateNotNull(trainer, TRAINER);
        getTrainerById(trainer.getUserId());

        return trainerDAO.update(trainer);
    }

    @Override
    public Trainer getTrainerById(Long id) {
        return trainerDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(TRAINER_NOT_FOUND_BY_ID, id)));
    }

    @Override
    public List<Trainer> getAllTrainers() {
        return trainerDAO.findAll();
    }
}
