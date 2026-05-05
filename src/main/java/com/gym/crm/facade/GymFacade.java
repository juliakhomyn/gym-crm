package com.gym.crm.facade;

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

    public TraineeResponseDTO createTrainee(TraineeRequestDTO traineeRequestDTO) {
        return traineeService.createTrainee(traineeRequestDTO);
    }

    public TraineeResponseDTO updateTrainee(TraineeUpdateDTO traineeUpdateDTO) {
        return traineeService.updateTrainee(traineeUpdateDTO);
    }

    public void deleteTraineeByUsername(String username) {
        traineeService.deleteByUsername(username);
    }

    public TraineeInfoDTO getTraineeByUsername(String username) {
        return traineeService.getTraineeByUsername(username);
    }

    public List<TraineeInfoDTO> getAllTrainees() {
        return traineeService.getAllTrainees();
    }

    public void updateTraineeTrainersList(TrainerAssignmentUpdateDTO dto) {
        traineeService.updateTrainersList(dto);
    }

    public TrainerResponseDTO createTrainer(TrainerRequestDTO trainerRequestDTO) {
        return trainerService.createTrainer(trainerRequestDTO);
    }

    public TrainerResponseDTO updateTrainer(TrainerUpdateDTO trainerUpdateDTO) {
        return trainerService.updateTrainer(trainerUpdateDTO);
    }

    public TrainerInfoDTO getTrainerByUsername(String username) {
        return trainerService.getTrainerByUsername(username);
    }

    public List<TrainerInfoDTO> getAllTrainers() {
        return trainerService.getAllTrainers();
    }

    public List<TrainerInfoDTO> getTrainersNotAssignedToTrainee(String username) {
        return trainerService.getNotAssignedToTrainee(username);
    }

    public void changePassword(PasswordChangeRequest request) {
        userService.changePassword(request);
    }

    public void toggleActiveStatus(ToggleActiveRequestDTO request) {
        userService.toggleActive(request);
    }

    public TrainingResponseDTO createTraining(TrainingRequestDTO trainingRequestDTO) {
        return trainingService.createTraining(trainingRequestDTO);
    }

    public TrainingResponseDTO getTrainingById(Long id) {
        return trainingService.getTrainingById(id);
    }

    public List<TrainingResponseDTO> getAllTrainings() {
        return trainingService.getAllTrainings();
    }

    public List<TrainingResponseDTO> getTraineeTrainingsByFilter(TraineeTrainingFilter filter) {
        return trainingService.getTraineeTrainings(filter);
    }

    public List<TrainingResponseDTO> getTrainerTrainingsByFilter(TrainerTrainingFilter filter) {
        return trainingService.getTrainerTrainings(filter);
    }
}
