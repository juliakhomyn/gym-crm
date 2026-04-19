package com.gym.crm.facade;

import com.gym.crm.dto.TraineeRequestDTO;
import com.gym.crm.dto.TraineeResponseDTO;
import com.gym.crm.dto.TraineeUpdateDTO;
import com.gym.crm.dto.TrainerRequestDTO;
import com.gym.crm.dto.TrainerResponseDTO;
import com.gym.crm.dto.TrainerUpdateDTO;
import com.gym.crm.dto.TrainingRequestDTO;
import com.gym.crm.dto.TrainingResponseDTO;
import com.gym.crm.mapper.TraineeMapper;
import com.gym.crm.mapper.TrainerMapper;
import com.gym.crm.mapper.TrainingMapper;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.TrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    private final TraineeMapper traineeMapper;
    private final TrainerMapper trainerMapper;
    private final TrainingMapper trainingMapper;

    public TraineeResponseDTO createTrainee(TraineeRequestDTO traineeRequestDTO) {
        Trainee trainee = traineeMapper.toEntity(traineeRequestDTO);
        Trainee saved = traineeService.createTrainee(trainee);

        return traineeMapper.toDto(saved);
    }

    public TraineeResponseDTO updateTrainee(TraineeUpdateDTO traineeUpdateDTO) {
        Trainee trainee = traineeMapper.toEntity(traineeUpdateDTO);
        Trainee saved = traineeService.updateTrainee(trainee);

        return traineeMapper.toDto(saved);
    }

    public void deleteTrainee(Long id) {
        traineeService.deleteTrainee(id);
    }

    public TraineeResponseDTO getTraineeById(Long id) {
        Trainee trainee = traineeService.getTraineeById(id);

        return traineeMapper.toDto(trainee);
    }

    public List<TraineeResponseDTO> getAllTrainees() {
        return traineeService.getAllTrainees()
                .stream()
                .map(traineeMapper::toDto)
                .toList();
    }

    public TrainerResponseDTO createTrainer(TrainerRequestDTO trainerRequestDTO) {
        Trainer trainer = trainerMapper.toEntity(trainerRequestDTO);
        Trainer saved = trainerService.createTrainer(trainer);

        return trainerMapper.toDto(saved);
    }

    public TrainerResponseDTO updateTrainer(TrainerUpdateDTO trainerUpdateDTO) {
        Trainer trainer = trainerMapper.toEntity(trainerUpdateDTO);
        Trainer saved = trainerService.createTrainer(trainer);

        return trainerMapper.toDto(saved);
    }

    public TrainerResponseDTO getTrainerById(Long id) {
        Trainer trainer = trainerService.getTrainerById(id);

        return trainerMapper.toDto(trainer);
    }

    public List<TrainerResponseDTO> getAllTrainers() {
        return trainerService.getAllTrainers()
                .stream()
                .map(trainerMapper::toDto)
                .toList();
    }

    public TrainingResponseDTO createTraining(TrainingRequestDTO trainingRequestDTO) {
        Training training = trainingMapper.toEntity(trainingRequestDTO);
        Training saved = trainingService.createTraining(training);

        return trainingMapper.toDto(saved);
    }

    public TrainingResponseDTO getTrainingById(Long id) {
        Training training = trainingService.getTrainingById(id);

        return trainingMapper.toDto(training);
    }

    public List<TrainingResponseDTO> getAllTrainings() {
        return trainingService.getAllTrainings()
                .stream()
                .map(trainingMapper::toDto)
                .toList();
    }
}
