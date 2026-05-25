package com.gym.crm.service.impl;

import com.gym.crm.dao.TraineeDAO;
import com.gym.crm.dao.TrainerDAO;
import com.gym.crm.dto.trainee.TraineeInfoDTO;
import com.gym.crm.dto.trainee.TraineeRequestDTO;
import com.gym.crm.dto.trainee.TraineeResponseDTO;
import com.gym.crm.dto.trainee.TraineeUpdateDTO;
import com.gym.crm.dto.trainee.TrainerAssignmentUpdateDTO;
import com.gym.crm.dto.trainer.TrainerInfoDTO;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.exception.ValidationFailedException;
import com.gym.crm.mapper.TraineeMapper;
import com.gym.crm.mapper.TrainerMapper;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.User;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.common.UserInputValidator;
import com.gym.crm.service.common.UserProfileService;
import com.gym.crm.transaction.Transactional;
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
public class TraineeServiceImpl implements TraineeService {
    private static final String TRAINEE_NOT_FOUND_BY_ID = "Trainee not found by id: %s";
    private static final String TRAINEE_NOT_FOUND_BY_USERNAME = "Trainee not found by username: %s";
    private static final String TRAINER_NOT_FOUND_BY_USERNAME = "Trainer not found by username: %s";
    private static final String TRAINEE = "Trainee";

    private final TraineeDAO dao;
    private final TrainerDAO trainerDAO;
    private final UserProfileService userProfileService;
    private final UserInputValidator userInputValidator;
    private final TraineeMapper mapper;
    private final TrainerMapper trainerMapper;

    @Transactional
    @Override
    public TraineeResponseDTO createTrainee(@Valid TraineeRequestDTO request) {
        userInputValidator.validate(request, TRAINEE);

        log.info("Creating trainee: firstName={}, lastName={}", request.getFirstName(), request.getLastName());

        Trainee trainee = mapper.toEntity(request);
        String username = userProfileService.generateUsername(request.getFirstName(), request.getLastName());
        String rawPassword = userProfileService.generatePassword();

        trainerDAO.findByUsername(username).ifPresent(trainer -> {
            log.info("Registration failed: user with username {} is already registered as trainer", username);
            throw new ValidationFailedException(String.format("User with username %s is already registered as a trainer", username));
        });

        User user = trainee.getUser().toBuilder()
                .username(username)
                .password(userProfileService.encodePassword(rawPassword))
                .isActive(true)
                .build();
        Trainee withCredentials = trainee.toBuilder()
                .user(user)
                .build();

        Trainee saved = dao.save(withCredentials);
        log.info("Trainee created successfully: username={}", saved.getUser().getUsername());

        TraineeResponseDTO response = mapper.toDto(saved);

        return response.toBuilder().password(rawPassword).build();
    }

    @Transactional
    @Override
    public TraineeResponseDTO updateTrainee(@Valid TraineeUpdateDTO request) {
        userInputValidator.validate(request, TRAINEE);

        log.info("Updating trainee: username={}", request.getUsername());

        Trainee existing = dao.findByUsername(request.getUsername()).orElseThrow(
                () -> new EntityNotFoundException(String.format(TRAINEE_NOT_FOUND_BY_USERNAME, request.getUsername())));

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .isActive(request.getIsActive())
                .build();
        Trainee updated = existing.toBuilder()
                .user(user)
                .dateOfBirth(request.getDateOfBirth())
                .address(request.getAddress())
                .build();

        Trainee saved = dao.update(updated);
        log.info("Trainee updated successfully: username={}", saved.getUser().getUsername());

        return mapper.toDto(saved);
    }

    @Transactional
    @Override
    public void deleteTraineeById(Long id) {
        userInputValidator.validateId(id);

        log.info("Deleting trainee by id: id={}", id);
        getTraineeById(id);

        dao.delete(id);
        log.info("Trainee deleted successfully: id={}", id);
    }

    @Transactional
    @Override
    public void deleteByUsername(String username) {
        userInputValidator.validateUsername(username);

        log.info("Deleting trainee by username: username={}", username);
        dao.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(String.format(TRAINEE_NOT_FOUND_BY_USERNAME, username)));

        dao.deleteByUsername(username);
        log.info("Trainee deleted successfully: username={}", username);
    }

    @Override
    public TraineeInfoDTO getTraineeById(Long id) {
        log.info("Getting trainee by id: id={}", id);
        userInputValidator.validateId(id);

        Trainee trainee = dao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(TRAINEE_NOT_FOUND_BY_ID, id)));

        return mapper.toInfoDto(trainee);
    }

    @Override
    public TraineeInfoDTO getTraineeByUsername(String username) {
        log.info("Getting trainee by username: username={}", username);
        userInputValidator.validateUsername(username);

        Trainee trainee = dao.findByUsernameWithTrainers(username)
                .orElseThrow(() -> new EntityNotFoundException(String.format(TRAINEE_NOT_FOUND_BY_USERNAME, username)));

        return mapper.toInfoDto(trainee);
    }

    @Override
    public List<TraineeInfoDTO> getAllTrainees() {
        log.info("Getting all trainees");

        return dao.findAll()
                .stream()
                .map(mapper::toInfoDto)
                .toList();
    }

    @Transactional
    @Override
    public List<TrainerInfoDTO> updateTrainersList(@Valid TrainerAssignmentUpdateDTO dto) {
        userInputValidator.validate(dto, "Trainer assignment");

        log.info("Updating trainers list for trainee: username={}, trainers' usernames={}", dto.getTraineeUsername(), dto.getTrainerUsernames());
        List<Trainer> trainers = dto.getTrainerUsernames().stream()
                .map(username -> trainerDAO.findByUsername(username)
                        .orElseThrow(() -> new EntityNotFoundException(String.format(TRAINER_NOT_FOUND_BY_USERNAME, username))))
                .toList();

        dao.updateTrainersList(dto.getTraineeUsername(), trainers);
        log.info("Trainers list updated successfully: username={}", dto.getTraineeUsername());

        Trainee updatedTrainee = dao.findByUsernameWithTrainers(dto.getTraineeUsername())
                .orElseThrow(() -> new EntityNotFoundException(String.format(TRAINEE_NOT_FOUND_BY_USERNAME, dto.getTraineeUsername())));

        return updatedTrainee.getTrainers().stream()
                .map(trainerMapper::toInfoDtoWithoutTrainees)
                .toList();
    }
}
