package com.gym.crm.mapper;

import com.gym.crm.dto.TrainingRequestDTO;
import com.gym.crm.dto.TrainingResponseDTO;
import com.gym.crm.model.Training;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TrainingMapper {

    @Mapping(target = "trainee.id", source = "traineeId")
    @Mapping(target = "trainer.id", source = "trainerId")
    @Mapping(target = "trainingType.trainingTypeName", source = "trainingTypeName")
    Training toEntity(TrainingRequestDTO trainingRequestDTO);

    @Mapping(target = "traineeId", source = "trainee.id")
    @Mapping(target = "trainerId", source = "trainer.id")
    @Mapping(target = "trainingTypeName", source = "trainingType.trainingTypeName")
    TrainingResponseDTO toDto(Training training);
}
