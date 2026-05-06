package com.gym.crm.service.impl;

import com.gym.crm.dao.TraineeDAO;
import com.gym.crm.dao.TrainerDAO;
import com.gym.crm.dao.TrainingDAO;
import com.gym.crm.dto.training.TrainingRequestDTO;
import com.gym.crm.dto.training.TrainingResponseDTO;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.mapper.TrainingMapper;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.search.filter.TraineeTrainingFilter;
import com.gym.crm.search.filter.TrainerTrainingFilter;
import com.gym.crm.service.TrainingService;
import com.gym.crm.service.common.UserInputValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {
    private static final String TRAINING_NOT_FOUND_BY_ID = "Training not found by id: %s";
    private static final String TRAINEE_NOT_FOUND_BY_USERNAME = "Trainee not found by username: %s";
    private static final String TRAINER_NOT_FOUND_BY_USERNAME = "Trainer not found by username: %s";
    private static final String TRAINING = "Training";

    private final TrainingDAO dao;
    private final TraineeDAO traineeDAO;
    private final TrainerDAO trainerDAO;
    private final UserInputValidator userInputValidator;
    private final TrainingMapper mapper;

    @Override
    public TrainingResponseDTO createTraining(@Valid TrainingRequestDTO request) {
        userInputValidator.validate(request, TRAINING);

        log.info("Creating training: trainingName={}", request.getTrainingName());

        Training mapped = mapper.toEntity(request);
        Training training = mapped.toBuilder()
                .trainee(getTrainee(request.getTraineeUsername()))
                .trainer(getTrainer(request.getTrainerUsername()))
                .build();

        Training saved = dao.save(training);
        log.info("Training created successfully: id={}", saved.getId());

        return mapper.toDto(saved);
    }

    @Override
    public TrainingResponseDTO getTrainingById(Long id) {
        log.info("Getting training by id: id={}", id);
        userInputValidator.validateId(id);

        Training training = dao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(TRAINING_NOT_FOUND_BY_ID, id)));

        return mapper.toDto(training);
    }

    @Override
    public List<TrainingResponseDTO> getAllTrainings() {
        log.info("Getting all trainings");

        return dao.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public List<TrainingResponseDTO> getTraineeTrainings(@Valid TraineeTrainingFilter filter) {
        userInputValidator.validate(filter, "Filter");
        log.info("Getting trainee trainings by filter: {}", filter);

        return dao.findByTraineeCriteria(filter)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public List<TrainingResponseDTO> getTrainerTrainings(@Valid TrainerTrainingFilter filter) {
        userInputValidator.validate(filter, "Filter");
        log.info("Getting trainer trainings by filter: {}", filter);

        return dao.findByTrainerCriteria(filter)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    private Trainee getTrainee(String username) {
        return traineeDAO.findByUsername(username).orElseThrow(() -> new EntityNotFoundException(String.format(TRAINEE_NOT_FOUND_BY_USERNAME, username)));
    }

    private Trainer getTrainer(String username) {
        return trainerDAO.findByUsername(username).orElseThrow(() -> new EntityNotFoundException(String.format(TRAINER_NOT_FOUND_BY_USERNAME, username)));
    }
}
