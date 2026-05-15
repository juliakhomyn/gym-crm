package com.gym.crm.mapper.rest;

import com.gia.openapi.model.AssignedTrainerResponse;
import com.gym.crm.dto.trainer.TrainerInfoDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TrainerRestMapper {

    AssignedTrainerResponse toRest(TrainerInfoDTO dto);
}
