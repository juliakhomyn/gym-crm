package com.gym.crm.service;

import com.gym.crm.dto.trainer.TrainerInfoDTO;
import com.gym.crm.dto.trainer.TrainerRequestDTO;
import com.gym.crm.dto.trainer.TrainerResponseDTO;
import com.gym.crm.dto.trainer.TrainerUpdateDTO;
import com.gym.crm.dto.validation.ValidId;
import com.gym.crm.dto.validation.ValidUsername;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface TrainerService {
    TrainerResponseDTO createTrainer(@Valid TrainerRequestDTO trainer);

    TrainerResponseDTO updateTrainer(@Valid TrainerUpdateDTO trainer);

    TrainerInfoDTO getTrainerById(@ValidId Long id);

    TrainerInfoDTO getTrainerByUsername(@ValidUsername String username);

    List<TrainerInfoDTO> getAllTrainers();

    List<TrainerInfoDTO> getNotAssignedToTrainee(@ValidUsername String traineeUsername);
}
