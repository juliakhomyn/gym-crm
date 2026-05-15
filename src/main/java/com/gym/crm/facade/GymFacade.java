package com.gym.crm.facade;

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
import com.gym.crm.auth.Authenticated;
import com.gym.crm.dto.common.AuthRequestDTO;
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
import com.gym.crm.mapper.rest.TraineeRestMapper;
import com.gym.crm.mapper.rest.TrainerRestMapper;
import com.gym.crm.search.filter.TraineeTrainingFilter;
import com.gym.crm.search.filter.TrainerTrainingFilter;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.TrainingService;
import com.gym.crm.service.UserService;
import com.gym.crm.service.common.AuthenticationService;
import com.gym.crm.service.common.UserInputValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final UserService userService;
    private final AuthenticationService authenticationService;

    private final TraineeRestMapper traineeRestMapper;
    private final TrainerRestMapper trainerRestMapper;

    public void login(LoginRequest request) {
        AuthRequestDTO dto = AuthRequestDTO.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .build();

        authenticationService.authenticate(dto);
    }

    @Authenticated
    public void logout(String username) {
        authenticationService.logout();
    }

    public TraineeCreateResponse createTrainee(TraineeCreateRequest request) {
        TraineeRequestDTO dto = traineeRestMapper.toDto(request);
        TraineeResponseDTO traineeResponseDTO = traineeService.createTrainee(dto);

        return traineeRestMapper.toRest(traineeResponseDTO);
    }

    @Authenticated
    public TraineeUpdateResponse updateTrainee(TraineeUpdateRequest request, String username) {
        TraineeUpdateDTO dto = traineeRestMapper.toDto(username, request);
        TraineeResponseDTO traineeResponseDTO = traineeService.updateTrainee(dto);

        return traineeRestMapper.toRestUpdateResponse(traineeResponseDTO);
    }

    @Authenticated
    public void deleteTraineeByUsername(String username) {
        traineeService.deleteByUsername(username);
    }

    @Authenticated
    public TraineeGetResponse getTraineeByUsername(String username) {
        TraineeInfoDTO traineeInfoDTO = traineeService.getTraineeByUsername(username);

        return traineeRestMapper.toRest(traineeInfoDTO);
    }

    @Authenticated
    public List<TraineeInfoDTO> getAllTrainees(String username) {
        return traineeService.getAllTrainees();
    }

    @Authenticated
    public TraineeAssignedTrainersUpdateResponse updateTraineeTrainersList(TraineeAssignedTrainersUpdateRequest request, String username) {
        TrainerAssignmentUpdateDTO dto = TrainerAssignmentUpdateDTO.builder()
                .traineeUsername(username)
                .trainerUsernames(request.getTrainerUsernames())
                .build();
        List<TrainerInfoDTO> list = traineeService.updateTrainersList(dto);
        List<AssignedTrainerResponse> assignedTrainers = list.stream()
                .map(trainerRestMapper::toRest)
                .toList();

        TraineeAssignedTrainersUpdateResponse response = new TraineeAssignedTrainersUpdateResponse();
        response.setTrainers(assignedTrainers);

        return response;
    }

    public TrainerResponseDTO createTrainer(TrainerRequestDTO trainerRequestDTO) {
        return trainerService.createTrainer(trainerRequestDTO);
    }

    @Authenticated
    public TrainerResponseDTO updateTrainer(TrainerUpdateDTO trainerUpdateDTO, String username) {
        return trainerService.updateTrainer(trainerUpdateDTO);
    }

    @Authenticated
    public TrainerInfoDTO getTrainerByUsername(String username) {
        return trainerService.getTrainerByUsername(username);
    }

    @Authenticated
    public List<TrainerInfoDTO> getAllTrainers(String username) {
        return trainerService.getAllTrainers();
    }

    @Authenticated
    public List<AssignedTrainerResponse> getTrainersNotAssignedToTrainee(String username) {
        List<TrainerInfoDTO> trainers = trainerService.getNotAssignedToTrainee(username);

        return trainers.stream()
                .map(trainerRestMapper::toRest)
                .toList();
    }

    @Authenticated
    public void changePassword(LoginChangeRequest request, String username) {
        PasswordChangeRequest requestDTO = PasswordChangeRequest.builder()
                .username(request.getUsername())
                .oldPassword(request.getOldPassword())
                .newPassword(request.getNewPassword())
                .build();

        userService.changePassword(requestDTO);
    }

    @Authenticated
    public void toggleActiveStatus(ActivationStatusRequest request, String username) {
        ToggleActiveRequestDTO dto = ToggleActiveRequestDTO.builder()
                .username(username)
                .isActive(request.getIsActive())
                .build();

        userService.toggleActive(dto);
    }

    @Authenticated
    public TrainingResponseDTO createTraining(TrainingRequestDTO trainingRequestDTO, String username) {
        return trainingService.createTraining(trainingRequestDTO);
    }

    @Authenticated
    public TrainingResponseDTO getTrainingById(Long id, String username) {
        return trainingService.getTrainingById(id);
    }

    @Authenticated
    public List<TrainingResponseDTO> getAllTrainings(String username) {
        return trainingService.getAllTrainings();
    }

    @Authenticated
    public List<TrainingResponseDTO> getTraineeTrainingsByFilter(TraineeTrainingFilter filter, String username) {
        return trainingService.getTraineeTrainings(filter);
    }

    @Authenticated
    public List<TrainingResponseDTO> getTrainerTrainingsByFilter(TrainerTrainingFilter filter, String username) {
        return trainingService.getTrainerTrainings(filter);
    }
}
