package com.gym.crm.service;

import com.gym.crm.dto.training.TrainingRequestDTO;
import com.gym.crm.dto.training.TrainingResponseDTO;
import com.gym.crm.dto.training.TrainingTypeDTO;
import com.gym.crm.search.filter.TraineeTrainingFilter;
import com.gym.crm.search.filter.TrainerTrainingFilter;

import java.util.List;

public interface TrainingService {
    TrainingResponseDTO createTraining(TrainingRequestDTO trainingRequestDTO);

    TrainingResponseDTO getTrainingById(Long id);

    List<TrainingResponseDTO> getAllTrainings();

    List<TrainingResponseDTO> getTraineeTrainings(TraineeTrainingFilter filter);

    List<TrainingResponseDTO> getTrainerTrainings(TrainerTrainingFilter filter);

    List<TrainingTypeDTO> getAllTrainingTypes();
}
