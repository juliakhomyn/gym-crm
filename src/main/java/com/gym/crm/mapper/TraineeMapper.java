package com.gym.crm.mapper;

import com.gym.crm.dto.trainee.TraineeInfoDTO;
import com.gym.crm.dto.trainee.TraineeRequestDTO;
import com.gym.crm.dto.trainee.TraineeResponseDTO;
import com.gym.crm.dto.trainee.TraineeUpdateDTO;
import com.gym.crm.dto.trainer.AssignedTrainerDTO;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.TrainingType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TraineeMapper {

    @Mapping(target = "user.firstName", source = "firstName")
    @Mapping(target = "user.lastName", source = "lastName")
    @Mapping(target = "dateOfBirth", source = "dateOfBirth")
    @Mapping(target = "address", source = "address")
    Trainee toEntity(TraineeRequestDTO traineeRequestDTO);

    @Mapping(target = "user.username", source = "username")
    @Mapping(target = "user.password", source = "password")
    @Mapping(target = "user.firstName", source = "firstName")
    @Mapping(target = "user.lastName", source = "lastName")
    @Mapping(target = "user.isActive", source = "isActive")
    @Mapping(target = "dateOfBirth", source = "dateOfBirth")
    @Mapping(target = "address", source = "address")
    Trainee toEntity(TraineeUpdateDTO traineeUpdateDTO);

    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "password", source = "user.password")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "isActive", source = "user.isActive")
    @Mapping(target = "dateOfBirth", source = "dateOfBirth")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "assignedTrainers", source = "trainers")
    TraineeResponseDTO toDto(Trainee trainee);

    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "isActive", source = "user.isActive")
    @Mapping(target = "dateOfBirth", source = "dateOfBirth")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "assignedTrainers", source = "trainers")
    TraineeInfoDTO toInfoDto(Trainee trainee);

    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "specialization", source = "specialization")
    AssignedTrainerDTO toAssignedTrainerDto(Trainer trainer);

    default String map(TrainingType type) {
        return type == null ? null : type.getTrainingTypeName();
    }
}
