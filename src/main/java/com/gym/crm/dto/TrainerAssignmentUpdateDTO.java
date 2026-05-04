package com.gym.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class TrainerAssignmentUpdateDTO {
    @NotBlank
    private final String traineeUsername;

    @NotEmpty
    private final List<String> trainerUsernames;
}
