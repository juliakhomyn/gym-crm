package com.gym.crm.facade.dto.trainer;

import com.gym.crm.facade.dto.trainee.AssignedTraineeDTO;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class TrainerInfoDTO {
    private final String firstName;
    private final String lastName;
    private final String username;
    private final Boolean isActive;
    private final String specialization;
    private final List<AssignedTraineeDTO> assignedTrainees;
}
