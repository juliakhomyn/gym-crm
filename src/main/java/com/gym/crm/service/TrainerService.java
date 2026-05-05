package com.gym.crm.service;

import com.gym.crm.dto.trainer.TrainerInfoDTO;
import com.gym.crm.dto.trainer.TrainerRequestDTO;
import com.gym.crm.dto.trainer.TrainerResponseDTO;
import com.gym.crm.dto.trainer.TrainerUpdateDTO;

import java.util.List;

public interface TrainerService {
    TrainerResponseDTO createTrainer(TrainerRequestDTO trainer);

    TrainerResponseDTO updateTrainer(TrainerUpdateDTO trainer);

    TrainerInfoDTO getTrainerById(Long id);

    TrainerInfoDTO getTrainerByUsername(String username);

    List<TrainerInfoDTO> getAllTrainers();

    List<TrainerInfoDTO> getNotAssignedToTrainee(String traineeUsername);
}
