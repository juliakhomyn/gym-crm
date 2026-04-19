package com.gym.crm.mapper;

import com.gym.crm.dto.TraineeRequestDTO;
import com.gym.crm.dto.TraineeResponseDTO;
import com.gym.crm.dto.TraineeUpdateDTO;
import com.gym.crm.model.Trainee;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TraineeMapper {
    Trainee toEntity(TraineeRequestDTO traineeRequestDTO);

    Trainee toEntity(TraineeUpdateDTO traineeUpdateDTO);

    TraineeResponseDTO toDto(Trainee trainee);
}
