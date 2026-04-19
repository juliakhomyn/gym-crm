package com.gym.crm.service.impl;

import com.gym.crm.dao.TraineeDAO;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.model.Trainee;
import com.gym.crm.service.TraineeService;
import com.gym.crm.util.UserCredentialGenerator;
import com.gym.crm.util.Validator;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TraineeServiceImpl implements TraineeService {
    private static final String TRAINEE_NOT_FOUND_BY_ID = "Trainee not found by id: %s";
    private static final String TRAINEE = "Trainee";

    @Setter(onMethod_={@Autowired})
    private TraineeDAO traineeDAO;

    @Setter(onMethod_={@Autowired})
    private UserCredentialGenerator userCredentialGenerator;

    @Setter(onMethod_={@Autowired})
    private PasswordEncoder passwordEncoder;

    @Override
    public Trainee createTrainee(Trainee trainee) {
        Validator.validateNotNull(trainee, TRAINEE);

        String username = userCredentialGenerator.generateUsername(trainee.getFirstName(), trainee.getLastName());
        String rawPassword = userCredentialGenerator.generatePassword();

        Trainee withCredentials = trainee.toBuilder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .isActive(true)
                .build();

        return traineeDAO.save(withCredentials);
    }

    @Override
    public Trainee updateTrainee(Trainee trainee) {
        Validator.validateNotNull(trainee, TRAINEE);
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
        return traineeDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(TRAINEE_NOT_FOUND_BY_ID, id)));
    }

    @Override
    public List<Trainee> getAllTrainees() {
        return traineeDAO.findAll();
    }
}
