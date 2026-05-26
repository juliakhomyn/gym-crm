package com.gym.crm.service.impl;

import com.gym.crm.dto.training.TrainingRequestDTO;
import com.gym.crm.dto.training.TrainingResponseDTO;
import com.gym.crm.dto.training.TrainingTypeDTO;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.mapper.TrainingMapper;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import com.gym.crm.repository.TraineeRepository;
import com.gym.crm.repository.TrainerRepository;
import com.gym.crm.repository.TrainingRepository;
import com.gym.crm.repository.TrainingTypeRepository;
import com.gym.crm.search.filter.TraineeTrainingFilter;
import com.gym.crm.search.filter.TrainerTrainingFilter;
import com.gym.crm.service.TrainingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {
    private static final String TRAINING_NOT_FOUND_BY_ID = "Training not found by id: %s";
    private static final String TRAINEE_NOT_FOUND_BY_USERNAME = "Trainee not found by username: %s";
    private static final String TRAINER_NOT_FOUND_BY_USERNAME = "Trainer not found by username: %s";
    private static final String TRAINING_TYPE_NOT_FOUND_BY_NAME = "Training type not found by name: %s";

    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainingMapper mapper;

    @Transactional
    @Override
    public TrainingResponseDTO createTraining(TrainingRequestDTO request) {
        log.info("Creating training: trainingName={}", request.getTrainingName());

        Training mapped = mapper.toEntity(request);

        Trainee trainee = traineeRepository.findByUserUsername(request.getTraineeUsername()).orElseThrow(
                () -> new EntityNotFoundException(String.format(TRAINEE_NOT_FOUND_BY_USERNAME, request.getTraineeUsername())));
        Trainer trainer = trainerRepository.findByUserUsername(request.getTrainerUsername()).orElseThrow(
                () -> new EntityNotFoundException(String.format(TRAINER_NOT_FOUND_BY_USERNAME, request.getTrainerUsername())));
        TrainingType trainingType = trainingTypeRepository.findByTrainingTypeName(request.getTrainingName()).orElseThrow(
                () -> new EntityNotFoundException(String.format(TRAINING_TYPE_NOT_FOUND_BY_NAME, request.getTrainingName())));

        Training training = mapped.toBuilder()
                .trainee(trainee)
                .trainer(trainer)
                .trainingType(trainingType)
                .build();

        Training saved = trainingRepository.save(training);
        log.info("Training created successfully: id={}", saved.getId());

        return mapper.toDto(saved);
    }

    @Override
    public TrainingResponseDTO getTrainingById(Long id) {
        log.info("Getting training by id: id={}", id);

        Training training = trainingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(TRAINING_NOT_FOUND_BY_ID, id)));

        return mapper.toDto(training);
    }

    @Override
    public List<TrainingResponseDTO> getAllTrainings() {
        log.info("Getting all trainings");

        return trainingRepository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public List<TrainingResponseDTO> getTraineeTrainings(TraineeTrainingFilter filter) {
        log.info("Getting trainee trainings by filter: {}", filter);

        return trainingRepository.findByTraineeCriteria(filter)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public List<TrainingResponseDTO> getTrainerTrainings(TrainerTrainingFilter filter) {
        log.info("Getting trainer trainings by filter: {}", filter);

        return trainingRepository.findByTrainerCriteria(filter)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public List<TrainingTypeDTO> getAllTrainingTypes() {
        return trainingTypeRepository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }
}
