package com.gym.crm.mapper;

import com.gym.crm.dto.TrainerRequestDTO;
import com.gym.crm.dto.TrainerResponseDTO;
import com.gym.crm.dto.TrainerUpdateDTO;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.TrainingType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TrainerMapper {
    Trainer toEntity(TrainerRequestDTO dto);

    Trainer toEntity(TrainerUpdateDTO dto);

    TrainerResponseDTO toDto(Trainer trainer);

    default TrainingType map(String type) {
        return type == null ? null : new TrainingType(type);
    }

    default String map(TrainingType type) {
        return type == null ? null : type.getTrainingTypeName();
    }
}
