package com.gym.crm.service;

import com.gym.crm.facade.dto.training.TrainingRequestDTO;
import com.gym.crm.facade.dto.training.TrainingResponseDTO;
import com.gym.crm.facade.dto.training.TrainingTypeDTO;
import com.gym.crm.facade.dto.validation.ValidId;
import com.gym.crm.search.filter.TraineeTrainingFilter;
import com.gym.crm.search.filter.TrainerTrainingFilter;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface TrainingService {
    TrainingResponseDTO createTraining(@Valid TrainingRequestDTO trainingRequestDTO);

    TrainingResponseDTO getTrainingById(@ValidId Long id);

    List<TrainingResponseDTO> getAllTrainings();

    List<TrainingResponseDTO> getTraineeTrainings(@Valid TraineeTrainingFilter filter);

    List<TrainingResponseDTO> getTrainerTrainings(@Valid TrainerTrainingFilter filter);

    List<TrainingTypeDTO> getAllTrainingTypes();
}
