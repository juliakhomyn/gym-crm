package com.gym.crm.service.impl;

import com.gym.crm.dao.TraineeDAO;
import com.gym.crm.dao.TrainerDAO;
import com.gym.crm.dao.TrainingTypeDAO;
import com.gym.crm.dto.trainer.TrainerInfoDTO;
import com.gym.crm.dto.trainer.TrainerRequestDTO;
import com.gym.crm.dto.trainer.TrainerResponseDTO;
import com.gym.crm.dto.trainer.TrainerUpdateDTO;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.exception.ValidationFailedException;
import com.gym.crm.mapper.TrainerMapper;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.TrainingType;
import com.gym.crm.model.User;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.common.UserInputValidator;
import com.gym.crm.service.common.UserProfileService;
import com.gym.crm.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {
    private static final String TRAINER_NOT_FOUND_BY_ID = "Trainer not found by id: %s";
    private static final String TRAINER_NOT_FOUND_BY_USERNAME = "Trainer not found by username: %s";
    private static final String TRAINING_TYPE_NOT_FOUND_BY_NAME = "Training type not found by name: %s";
    private static final String TRAINER = "Trainer";

    private final TrainerDAO dao;
    private final TraineeDAO traineeDAO;
    private final TrainingTypeDAO trainingTypeDAO;
    private final UserProfileService userProfileService;
    private final UserInputValidator userInputValidator;
    private final TrainerMapper mapper;

    @Transactional
    @Override
    public TrainerResponseDTO createTrainer(@Valid TrainerRequestDTO request) {
        userInputValidator.validate(request, TRAINER);

        log.info("Creating trainer: firstName={} lastName={}", request.getFirstName(), request.getLastName());

        Trainer trainer = mapper.toEntity(request);
        String username = userProfileService.generateUsername(request.getFirstName(), request.getLastName());
        String rawPassword = userProfileService.generatePassword();

        traineeDAO.findByUsername(username).ifPresent(trainee -> {
            log.info("Registration failed: user with username {} is already registered as trainee", username);
            throw new ValidationFailedException(String.format("User with username %s is already registered as a trainee", username));
        });

        TrainingType trainingType = trainingTypeDAO.findByTrainingTypeName(request.getSpecialization()).orElseThrow(
                () -> new EntityNotFoundException(String.format(TRAINING_TYPE_NOT_FOUND_BY_NAME, request.getSpecialization())));

        User user = trainer.getUser().toBuilder()
                .username(username)
                .password(userProfileService.encodePassword(rawPassword))
                .isActive(true)
                .build();
        Trainer withCredentials = trainer.toBuilder()
                .user(user)
                .specialization(trainingType)
                .build();

        Trainer saved = dao.save(withCredentials);
        log.info("Trainer created successfully: username={}", saved.getUser().getUsername());

        return mapper.toDto(saved);
    }

    @Transactional
    @Override
    public TrainerResponseDTO updateTrainer(@Valid TrainerUpdateDTO request) {
        userInputValidator.validate(request, TRAINER);

        log.info("Updating trainer: username={}", request.getUsername());

        Trainer existing = dao.findByUsername(request.getUsername()).orElseThrow(
                () -> new EntityNotFoundException(String.format(TRAINER_NOT_FOUND_BY_USERNAME, request.getUsername())));
        TrainingType trainingType = trainingTypeDAO.findByTrainingTypeName(request.getSpecialization()).orElseThrow(
                () -> new EntityNotFoundException(String.format(TRAINING_TYPE_NOT_FOUND_BY_NAME, request.getSpecialization())));

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .isActive(request.getIsActive())
                .build();
        Trainer updated = existing.toBuilder()
                .user(user)
                .specialization(trainingType)
                .build();

        Trainer saved = dao.update(updated);
        log.info("Trainer updated successfully: username={}", saved.getUser().getUsername());

        return mapper.toDto(saved);
    }

    @Override
    public TrainerInfoDTO getTrainerById(Long id) {
        log.info("Getting trainer by id: id={}", id);
        userInputValidator.validateId(id);

        Trainer trainer = dao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(TRAINER_NOT_FOUND_BY_ID, id)));

        return mapper.toInfoDto(trainer);
    }

    @Override
    public TrainerInfoDTO getTrainerByUsername(String username) {
        log.info("Getting trainer by username: username={}", username);
        userInputValidator.validateUsername(username);

        Trainer trainer = dao.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(String.format(TRAINER_NOT_FOUND_BY_USERNAME, username)));

        return mapper.toInfoDto(trainer);
    }

    @Override
    public List<TrainerInfoDTO> getAllTrainers() {
        log.info("Getting all trainers");

        return dao.findAll()
                .stream()
                .map(mapper::toInfoDto)
                .toList();
    }

    @Override
    public List<TrainerInfoDTO> getNotAssignedToTrainee(String traineeUsername) {
        userInputValidator.validateUsername(traineeUsername);

        log.info("Getting all trainers not assigned to trainee: username={}", traineeUsername);

        return dao.findNotAssignedToTrainee(traineeUsername).stream()
                .map(mapper::toInfoDto)
                .toList();
    }
}
