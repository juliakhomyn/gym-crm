package com.gym.crm.service;

import com.gym.crm.dto.trainee.TraineeInfoDTO;
import com.gym.crm.dto.trainee.TraineeRequestDTO;
import com.gym.crm.dto.trainee.TraineeResponseDTO;
import com.gym.crm.dto.trainee.TraineeUpdateDTO;
import com.gym.crm.dto.trainee.TrainerAssignmentUpdateDTO;
import com.gym.crm.dto.trainer.TrainerInfoDTO;

import java.util.List;

public interface TraineeService {
    TraineeResponseDTO createTrainee(TraineeRequestDTO trainee);

    TraineeResponseDTO updateTrainee(TraineeUpdateDTO trainee);

    void deleteTraineeById(Long id);

    void deleteByUsername(String username);

    TraineeInfoDTO getTraineeById(Long id);

    TraineeInfoDTO getTraineeByUsername(String username);

    List<TraineeInfoDTO> getAllTrainees();

    List<TrainerInfoDTO> updateTrainersList(TrainerAssignmentUpdateDTO dto);
}
