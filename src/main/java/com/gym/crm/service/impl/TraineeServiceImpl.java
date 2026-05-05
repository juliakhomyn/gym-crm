package com.gym.crm.service.impl;

import com.gym.crm.dao.TraineeDAO;
import com.gym.crm.dao.TrainerDAO;
import com.gym.crm.dto.trainee.TraineeInfoDTO;
import com.gym.crm.dto.trainee.TraineeRequestDTO;
import com.gym.crm.dto.trainee.TraineeResponseDTO;
import com.gym.crm.dto.trainee.TraineeUpdateDTO;
import com.gym.crm.dto.trainee.TrainerAssignmentUpdateDTO;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.mapper.TraineeMapper;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.User;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.common.ValidationService;
import com.gym.crm.util.UserCredentialGenerator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class TraineeServiceImpl implements TraineeService {
    private static final String TRAINEE_NOT_FOUND_BY_ID = "Trainee not found by id: %s";
    private static final String TRAINEE_NOT_FOUND_BY_USERNAME = "Trainee not found by username: %s";
    private static final String TRAINER_NOT_FOUND_BY_USERNAME = "Trainer not found by username: %s";
    private static final String TRAINEE = "Trainee";

    private TraineeDAO dao;
    private TrainerDAO trainerDAO;
    private UserCredentialGenerator userCredentialGenerator;
    private PasswordEncoder passwordEncoder;
    private ValidationService validationService;
    private TraineeMapper mapper;

    @Override
    public TraineeResponseDTO createTrainee(@Valid TraineeRequestDTO request) {
        validationService.validate(request, TRAINEE);

        log.info("Creating trainee: firstName={} lastName{}", request.getFirstName(), request.getLastName());

        Trainee trainee = mapper.toEntity(request);
        String username = userCredentialGenerator.generateUsername(request.getFirstName(), request.getLastName());
        String rawPassword = userCredentialGenerator.generatePassword();

        User user = trainee.getUser().toBuilder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .isActive(true)
                .build();
        Trainee withCredentials = trainee.toBuilder()
                .user(user)
                .build();

        Trainee saved = dao.save(withCredentials);
        log.info("Trainee created successfully: username={}", saved.getUser().getUsername());

        return mapper.toDto(saved);
    }

    @Override
    public TraineeResponseDTO updateTrainee(@Valid TraineeUpdateDTO request) {
        validationService.validate(request, TRAINEE);

        Trainee trainee = mapper.toEntity(request);

        log.info("Updating trainee: id={}", trainee.getId());
        getTraineeById(trainee.getId());

        Trainee updated = dao.update(trainee);
        log.info("Trainee updated successfully: id={}", updated.getId());

        return mapper.toDto(updated);
    }

    @Override
    public void deleteTraineeById(Long id) {
        validationService.validateId(id);

        log.info("Deleting trainee by id: id={}", id);
        getTraineeById(id);

        dao.delete(id);
        log.info("Trainee deleted successfully: id={}", id);
    }

    @Override
    public void deleteByUsername(String username) {
        validationService.validateUsername(username);

        log.info("Deleting trainee by username: username={}", username);
        getTraineeByUsername(username);

        dao.deleteByUsername(username);
        log.info("Trainee deleted successfully: username={}", username);
    }

    @Override
    public TraineeInfoDTO getTraineeById(Long id) {
        log.info("Getting trainee by id: id={}", id);
        validationService.validateId(id);

        Trainee trainee = dao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(TRAINEE_NOT_FOUND_BY_ID, id)));

        return mapper.toInfoDto(trainee);
    }

    @Override
    public TraineeInfoDTO getTraineeByUsername(String username) {
        log.info("Getting trainee by username: username={}", username);
        validationService.validateUsername(username);

        Trainee trainee = dao.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(String.format(TRAINEE_NOT_FOUND_BY_USERNAME, username)));

        return mapper.toInfoDto(trainee);
    }

    @Override
    public List<TraineeInfoDTO> getAllTrainees() {
        log.info("Getting all trainees");

        return dao.findAll()
                .stream()
                .map(t -> mapper.toInfoDto(t))
                .toList();
    }

    @Override
    public void updateTrainersList(@Valid TrainerAssignmentUpdateDTO dto) {
        validationService.validate(dto, "Trainer assignment");

        log.info("Updating trainers list for trainee: username={}, trainers' usernames={}", dto.getTraineeUsername(), dto.getTrainerUsernames());
        List<Trainer> trainers = dto.getTrainerUsernames().stream()
                .map(username -> trainerDAO.findByUsername(username)
                        .orElseThrow(() -> new EntityNotFoundException(String.format(TRAINER_NOT_FOUND_BY_USERNAME, username))))
                .toList();

        dao.updateTrainersList(dto.getTraineeUsername(), trainers);
        log.info("Trainers list updated successfully: username={}", dto.getTraineeUsername());
    }
}
