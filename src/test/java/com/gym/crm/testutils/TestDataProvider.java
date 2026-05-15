package com.gym.crm.testutils;

import com.gia.openapi.model.ActivationStatusRequest;
import com.gia.openapi.model.AssignedTrainerResponse;
import com.gia.openapi.model.LoginChangeRequest;
import com.gia.openapi.model.LoginRequest;
import com.gia.openapi.model.TraineeAssignedTrainersUpdateRequest;
import com.gia.openapi.model.TraineeAssignedTrainersUpdateResponse;
import com.gia.openapi.model.TraineeCreateRequest;
import com.gia.openapi.model.TraineeCreateResponse;
import com.gia.openapi.model.TraineeGetResponse;
import com.gia.openapi.model.TraineeUpdateRequest;
import com.gia.openapi.model.TraineeUpdateResponse;
import com.gym.crm.dto.common.AuthRequestDTO;
import com.gym.crm.dto.common.AuthResponseDTO;
import com.gym.crm.dto.common.PasswordChangeRequest;
import com.gym.crm.dto.common.ToggleActiveRequestDTO;
import com.gym.crm.dto.trainee.TraineeInfoDTO;
import com.gym.crm.dto.trainee.TraineeRequestDTO;
import com.gym.crm.dto.trainee.TraineeResponseDTO;
import com.gym.crm.dto.trainee.TraineeUpdateDTO;
import com.gym.crm.dto.trainee.TrainerAssignmentUpdateDTO;
import com.gym.crm.dto.trainer.TrainerInfoDTO;
import com.gym.crm.dto.trainer.TrainerRequestDTO;
import com.gym.crm.dto.trainer.TrainerResponseDTO;
import com.gym.crm.dto.trainer.TrainerUpdateDTO;
import com.gym.crm.dto.training.TrainingRequestDTO;
import com.gym.crm.dto.training.TrainingResponseDTO;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.User;
import com.gym.crm.search.filter.TraineeTrainingFilter;
import com.gym.crm.search.filter.TrainerTrainingFilter;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class TestDataProvider {
    private static final String FIRST_NAME = "Simone";
    private static final String LAST_NAME = "Radcliffe";
    private static final String USERNAME = "Simone.Radcliffe";
    private static final String PASSWORD = "password";
    private static final String NEW_PASSWORD = "newPassword";
    private static final LocalDate DATE_OF_BIRTH = LocalDate.of(2000, 1, 1);
    private static final String ADDRESS = "123 Main St";
    private static final String TRAINER_USERNAME1 = "trainer1";
    private static final String TRAINER_USERNAME2 = "trainer2";
    private static final String TRAINING_NAME = "Morning Cardio";
    private static final String TRAINING_TYPE_NAME = "Cardio";
    private static final String TRAINER_FIRST_NAME = "Owen";
    private static final String TRAINER_LAST_NAME = "Castleberry";
    private static final String TRAINER_USERNAME = "Owen.Castleberry";
    private static final String SPECIALIZATION = "Yoga";
    private static final String NOT_FOUND_USERNAME = "Not.Found";
    private static final String ENCODED_PASSWORD = "encodedPassword";
    private static final long VALID_ID = 1L;
    private static final long NOT_FOUND_ID = 999L;

    private static final String AUTH_SUCCESS_MESSAGE = "Authentication successful!";

    public static TraineeRequestDTO buildTraineeRequestDTO() {
        return TraineeRequestDTO.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .build();
    }

    public static TraineeUpdateDTO buildTraineeUpdateDTO() {
        return TraineeUpdateDTO.builder()
                .username(USERNAME)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .build();
    }

    public static TraineeResponseDTO buildTraineeResponseDTO() {
        return TraineeResponseDTO.builder()
                .id(VALID_ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .username(USERNAME)
                .isActive(true)
                .build();
    }

    public static TraineeInfoDTO buildTraineeInfoDTO() {
        return TraineeInfoDTO.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .isActive(true)
                .dateOfBirth(DATE_OF_BIRTH)
                .address(ADDRESS)
                .build();
    }

    public static TrainerRequestDTO buildTrainerRequestDTO() {
        return TrainerRequestDTO.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .build();
    }

    public static TrainerUpdateDTO buildTrainerUpdateDTO() {
        return TrainerUpdateDTO.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .build();
    }

    public static TrainerResponseDTO buildTrainerResponseDTO() {
        return TrainerResponseDTO.builder()
                .id(2L)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .username(USERNAME)
                .isActive(true)
                .build();
    }

    public static TrainerInfoDTO buildTrainerInfoDTO() {
        return TrainerInfoDTO.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .username(USERNAME)
                .isActive(true)
                .specialization(TRAINING_TYPE_NAME)
                .build();
    }

    public static TrainingRequestDTO buildTrainingRequestDTO() {
        return TrainingRequestDTO.builder()
                .traineeUsername(USERNAME)
                .trainerUsername(USERNAME)
                .trainingName(TRAINING_NAME)
                .trainingTypeName(TRAINING_TYPE_NAME)
                .trainingDate(LocalDate.of(2024, 1, 15))
                .trainingDuration(60)
                .build();
    }

    public static TrainingResponseDTO buildTrainingResponseDTO() {
        return TrainingResponseDTO.builder()
                .id(VALID_ID)
                .traineeUsername(USERNAME)
                .trainerUsername(USERNAME)
                .trainingName(TRAINING_NAME)
                .trainingTypeName(TRAINING_TYPE_NAME)
                .trainingDate(LocalDate.of(2024, 1, 15))
                .trainingDuration(60)
                .build();
    }

    public static ToggleActiveRequestDTO buildToggleActiveRequestDTO() {
        return ToggleActiveRequestDTO.builder()
                .username(USERNAME)
                .isActive(true)
                .build();
    }

    public static TrainerAssignmentUpdateDTO buildTrainerAssignmentUpdateDTO() {
        return TrainerAssignmentUpdateDTO.builder()
                .traineeUsername(USERNAME)
                .trainerUsernames(List.of(TRAINER_USERNAME))
                .build();
    }

    public static PasswordChangeRequest buildPasswordChangeRequest() {
        return PasswordChangeRequest.builder()
                .username(USERNAME)
                .oldPassword(PASSWORD)
                .newPassword("newPassword")
                .build();
    }

    public static TraineeTrainingFilter buildTraineeTrainingFilter() {
        return TraineeTrainingFilter.builder()
                .username(USERNAME)
                .build();
    }

    public static TrainerTrainingFilter buildTrainerTrainingFilter() {
        return TrainerTrainingFilter.builder()
                .username(USERNAME)
                .build();
    }

    public static AuthRequestDTO buildAuthRequestDTO() {
        return AuthRequestDTO.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .build();
    }

    public static AuthResponseDTO buildAuthResponseDTO() {
        return AuthResponseDTO.builder()
                .username(USERNAME)
                .message(AUTH_SUCCESS_MESSAGE)
                .build();
    }

    public static LoginRequest buildLoginRequest() {
        return new LoginRequest(USERNAME, PASSWORD);
    }

    public static LoginChangeRequest buildLoginChangeRequest() {
        return new LoginChangeRequest(USERNAME, PASSWORD, NEW_PASSWORD);
    }

    public static TraineeCreateRequest buildTraineeCreateRequest() {
        TraineeCreateRequest request = new TraineeCreateRequest();
        request.setFirstName(FIRST_NAME);
        request.setLastName(LAST_NAME);
        request.setDateOfBirth(DATE_OF_BIRTH);
        request.setAddress(ADDRESS);

        return request;
    }

    public static TraineeCreateRequest buildTraineeCreateRequestOnlyRequiredFields() {
        TraineeCreateRequest request = new TraineeCreateRequest();
        request.setFirstName(FIRST_NAME);
        request.setLastName(LAST_NAME);

        return request;
    }

    public static TraineeCreateResponse buildTraineeCreateResponse() {
        return new TraineeCreateResponse(USERNAME, PASSWORD);
    }

    public static TraineeUpdateRequest buildTraineeUpdateRequest() {
        TraineeUpdateRequest request = new TraineeUpdateRequest();
        request.setFirstName(FIRST_NAME);
        request.setLastName(LAST_NAME);
        request.setDateOfBirth(DATE_OF_BIRTH);
        request.setAddress(ADDRESS);
        request.isActive(true);

        return request;
    }

    public static TraineeUpdateResponse buildTraineeUpdateResponse() {
        TraineeUpdateResponse response = new TraineeUpdateResponse();
        response.setFirstName(FIRST_NAME);
        response.setLastName(LAST_NAME);
        response.setDateOfBirth(DATE_OF_BIRTH);
        response.setAddress(ADDRESS);
        response.isActive(true);
        response.setTrainers(List.of(buildAssignedTrainerResponse()));

        return response;
    }

    public static AssignedTrainerResponse buildAssignedTrainerResponse() {
        AssignedTrainerResponse response = new AssignedTrainerResponse();
        response.setUsername(TRAINER_USERNAME);
        response.setFirstName(TRAINER_FIRST_NAME);
        response.setLastName(TRAINER_LAST_NAME);
        response.setSpecialization(SPECIALIZATION);

        return response;
    }

    public static ActivationStatusRequest buildActivationStatusRequest() {
        return new ActivationStatusRequest(true);
    }

    public static TraineeGetResponse buildTraineeGetResponse() {
        TraineeGetResponse response = new TraineeGetResponse();
        response.setFirstName(FIRST_NAME);
        response.setLastName(LAST_NAME);
        response.setDateOfBirth(DATE_OF_BIRTH);
        response.setAddress(ADDRESS);
        response.isActive(true);
        response.setTrainers(List.of(buildAssignedTrainerResponse()));

        return response;
    }

    public static TraineeAssignedTrainersUpdateRequest buildTraineeAssignedTrainersUpdateRequest() {
        TraineeAssignedTrainersUpdateRequest request = new TraineeAssignedTrainersUpdateRequest();
        request.setTrainerUsernames(List.of(TRAINER_USERNAME));

        return request;
    }

    public static TraineeAssignedTrainersUpdateResponse buildTraineeAssignedTrainersUpdateResponse() {
        TraineeAssignedTrainersUpdateResponse response = new TraineeAssignedTrainersUpdateResponse();
        response.setTrainers(List.of(buildAssignedTrainerResponse()));

        return response;
    }

    public static Trainee buildTrainee() {
        return Trainee.builder()
                .user(buildUser())
                .dateOfBirth(DATE_OF_BIRTH)
                .address(ADDRESS)
                .build();
    }

    public static User buildUser() {
        return User.builder()
                .id(VALID_ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .username(USERNAME)
                .password(ENCODED_PASSWORD)
                .isActive(true)
                .build();
    }

    public static User buildSavedUser() {
        return User.builder()
                .id(VALID_ID)
                .username(USERNAME)
                .password(ENCODED_PASSWORD)
                .isActive(true)
                .build();
    }

    public static Trainee buildSavedTrainee() {
        return buildTrainee().toBuilder()
                .id(VALID_ID)
                .user(buildSavedUser())
                .build();
    }

    public static TraineeUpdateDTO buildNonExistentTraineeUpdateDTO() {
        return TraineeUpdateDTO.builder()
                .id(NOT_FOUND_ID)
                .username(NOT_FOUND_USERNAME)
                .build();
    }

    public static TrainerAssignmentUpdateDTO buildValidTrainerAssignmentUpdateDto() {
        return TrainerAssignmentUpdateDTO.builder()
                .traineeUsername(USERNAME)
                .trainerUsernames(List.of(TRAINER_USERNAME1, TRAINER_USERNAME2))
                .build();
    }

    public static Trainer buildTrainer(Long id, String username) {
        return Trainer.builder()
                .id(id)
                .user(User.builder().id(id).username(username).build())
                .build();
    }

    public static Trainee buildTraineeWithTrainers(Set<Trainer> trainers) {
        return buildTrainee().toBuilder()
                .trainers(trainers)
                .build();
    }

    public static TrainerInfoDTO buildTrainerInfoDTO(String username) {
        return TrainerInfoDTO.builder()
                .username(username)
                .firstName(TRAINER_FIRST_NAME)
                .lastName(TRAINER_LAST_NAME)
                .specialization(SPECIALIZATION)
                .isActive(true)
                .build();
    }
}
