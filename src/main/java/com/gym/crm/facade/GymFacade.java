package com.gym.crm.facade;

import com.gym.crm.auth.Authenticated;
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
import com.gym.crm.search.filter.TraineeTrainingFilter;
import com.gym.crm.search.filter.TrainerTrainingFilter;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.TrainingService;
import com.gym.crm.service.UserService;
import com.gym.crm.service.common.AuthenticationService;
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

    public AuthResponseDTO login(AuthRequestDTO dto) {
        return authenticationService.authenticate(dto);
    }

    @Authenticated
    public void logout(String callerUsername) {
        authenticationService.logout();
    }

    public TraineeResponseDTO createTrainee(TraineeRequestDTO traineeRequestDTO) {
        return traineeService.createTrainee(traineeRequestDTO);
    }

    @Authenticated
    public TraineeResponseDTO updateTrainee(TraineeUpdateDTO traineeUpdateDTO, String callerUsername) {
        return traineeService.updateTrainee(traineeUpdateDTO);
    }

    @Authenticated
    public void deleteTraineeByUsername(String username, String callerUsername) {
        traineeService.deleteByUsername(username);
    }

    @Authenticated
    public TraineeInfoDTO getTraineeByUsername(String username, String callerUsername) {
        return traineeService.getTraineeByUsername(username);
    }

    @Authenticated
    public List<TraineeInfoDTO> getAllTrainees(String callerUsername) {
        return traineeService.getAllTrainees();
    }

    @Authenticated
    public void updateTraineeTrainersList(TrainerAssignmentUpdateDTO dto, String callerUsername) {
        traineeService.updateTrainersList(dto);
    }

    public TrainerResponseDTO createTrainer(TrainerRequestDTO trainerRequestDTO) {
        return trainerService.createTrainer(trainerRequestDTO);
    }

    @Authenticated
    public TrainerResponseDTO updateTrainer(TrainerUpdateDTO trainerUpdateDTO, String callerUsername) {
        return trainerService.updateTrainer(trainerUpdateDTO);
    }

    @Authenticated
    public TrainerInfoDTO getTrainerByUsername(String username, String callerUsername) {
        return trainerService.getTrainerByUsername(username);
    }

    @Authenticated
    public List<TrainerInfoDTO> getAllTrainers(String callerUsername) {
        return trainerService.getAllTrainers();
    }

    @Authenticated
    public List<TrainerInfoDTO> getTrainersNotAssignedToTrainee(String username, String callerUsername) {
        return trainerService.getNotAssignedToTrainee(username);
    }

    @Authenticated
    public void changePassword(PasswordChangeRequest request, String callerUsername) {
        userService.changePassword(request);
    }

    @Authenticated
    public void toggleActiveStatus(ToggleActiveRequestDTO request, String callerUsername) {
        userService.toggleActive(request);
    }

    @Authenticated
    public TrainingResponseDTO createTraining(TrainingRequestDTO trainingRequestDTO, String callerUsername) {
        return trainingService.createTraining(trainingRequestDTO);
    }

    @Authenticated
    public TrainingResponseDTO getTrainingById(Long id, String callerUsername) {
        return trainingService.getTrainingById(id);
    }

    @Authenticated
    public List<TrainingResponseDTO> getAllTrainings(String callerUsername) {
        return trainingService.getAllTrainings();
    }

    @Authenticated
    public List<TrainingResponseDTO> getTraineeTrainingsByFilter(TraineeTrainingFilter filter, String callerUsername) {
        return trainingService.getTraineeTrainings(filter);
    }

    @Authenticated
    public List<TrainingResponseDTO> getTrainerTrainingsByFilter(TrainerTrainingFilter filter, String callerUsername) {
        return trainingService.getTrainerTrainings(filter);
    }
}
