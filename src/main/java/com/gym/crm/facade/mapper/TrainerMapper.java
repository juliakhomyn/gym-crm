package com.gym.crm.facade.mapper;

import com.gym.crm.facade.dto.trainee.AssignedTraineeDTO;
import com.gym.crm.facade.dto.trainer.TrainerInfoDTO;
import com.gym.crm.facade.dto.trainer.TrainerRequestDTO;
import com.gym.crm.facade.dto.trainer.TrainerResponseDTO;
import com.gym.crm.facade.dto.trainer.TrainerUpdateDTO;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.TrainingType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainerMapper {

    @Mapping(target = "user.firstName", source = "firstName")
    @Mapping(target = "user.lastName", source = "lastName")
    @Mapping(target = "specialization", ignore = true)
    Trainer toEntity(TrainerRequestDTO trainerRequestDTO);

    @Mapping(target = "user.username", source = "username")
    @Mapping(target = "user.password", source = "password")
    @Mapping(target = "user.firstName", source = "firstName")
    @Mapping(target = "user.lastName", source = "lastName")
    @Mapping(target = "user.isActive", source = "isActive")
    @Mapping(target = "specialization", source = "specialization")
    Trainer toEntity(TrainerUpdateDTO trainerUpdateDTO);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "password", source = "user.password")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "isActive", source = "user.isActive")
    @Mapping(target = "specialization", source = "specialization")
    TrainerResponseDTO toDto(Trainer trainer);

    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "isActive", source = "user.isActive")
    @Mapping(target = "specialization", source = "specialization")
    @Mapping(target = "assignedTrainees", source = "trainees")
    TrainerInfoDTO toInfoDto(Trainer trainer);

    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "isActive", source = "user.isActive")
    @Mapping(target = "specialization", source = "specialization")
    @Mapping(target = "assignedTrainees", ignore = true)
    TrainerInfoDTO toInfoDtoWithoutTrainees(Trainer trainer);

    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    AssignedTraineeDTO toAssignedTraineeDto(Trainee trainee);

    default TrainingType map(String type) {
        return type == null ? null : TrainingType.builder().trainingTypeName(type).build();
    }

    default String map(TrainingType type) {
        return type == null ? null : type.getTrainingTypeName();
    }
}
