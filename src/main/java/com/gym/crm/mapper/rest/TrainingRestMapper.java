package com.gym.crm.mapper.rest;

import com.gia.openapi.model.GetTraineeTrainingResponse;
import com.gia.openapi.model.GetTrainerTrainingResponse;
import com.gia.openapi.model.TrainingCreateRequest;
import com.gia.openapi.model.TrainingTypeResponse;
import com.gym.crm.dto.training.TrainingRequestDTO;
import com.gym.crm.dto.training.TrainingResponseDTO;
import com.gym.crm.dto.training.TrainingTypeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainingRestMapper {

    TrainingRequestDTO toDto(TrainingCreateRequest request);

    TrainingTypeResponse toRest(TrainingTypeDTO dto);

    @Mapping(target = "trainingType", source = "trainingTypeName")
    @Mapping(target = "trainerName", source = "trainerUsername")
    GetTraineeTrainingResponse toRestTraineeResponse(TrainingResponseDTO dto);

    @Mapping(target = "trainingType", source = "trainingTypeName")
    @Mapping(target = "traineeName", source = "traineeUsername")
    GetTrainerTrainingResponse toRestTrainerResponse(TrainingResponseDTO dto);
}
