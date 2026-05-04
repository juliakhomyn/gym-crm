package com.gym.crm.service.impl;

import com.gym.crm.dao.TrainerDAO;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.User;
import com.gym.crm.service.TrainerService;
import com.gym.crm.util.UserCredentialGenerator;
import com.gym.crm.util.Validator;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TrainerServiceImpl implements TrainerService {
    private static final String TRAINER_NOT_FOUND_BY_ID = "Trainer not found by id: %s";
    private static final String TRAINER = "Trainer";

    @Setter(onMethod_={@Autowired})
    private TrainerDAO dao;

    @Setter(onMethod_={@Autowired})
    private UserCredentialGenerator userCredentialGenerator;

    @Setter(onMethod_={@Autowired})
    private PasswordEncoder passwordEncoder;

    @Override
    public Trainer createTrainer(Trainer trainer) {
        Validator.validateNotNull(trainer, TRAINER);

        log.info("Creating trainer: firstName={} lastName{}", trainer.getUser().getFirstName(), trainer.getUser().getLastName());

        String username = userCredentialGenerator.generateUsername(trainer.getUser().getFirstName(), trainer.getUser().getLastName());
        String rawPassword = userCredentialGenerator.generatePassword();

        User user = trainer.getUser().toBuilder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .isActive(true)
                .build();
        Trainer withCredentials = trainer.toBuilder()
                .user(user)
                .build();

        Trainer saved = dao.save(withCredentials);
        log.info("Trainer created successfully: username={}", saved.getUser().getUsername());

        return saved;
    }

    @Override
    public Trainer updateTrainer(Trainer trainer) {
        Validator.validateNotNull(trainer, TRAINER);

        log.info("Updating trainer: id={}", trainer.getId());
        getTrainerById(trainer.getId());

        Trainer updated = dao.update(trainer);
        log.info("Trainer updated successfully: id={}", updated.getId());

        return updated;
    }

    @Override
    public Trainer getTrainerById(Long id) {
        return dao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(TRAINER_NOT_FOUND_BY_ID, id)));
    }

    @Override
    public List<Trainer> getAllTrainers() {
        return dao.findAll();
    }
}
