package com.gym.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class TrainingRequestDTO {
    private final Long traineeId;
    private final Long trainerId;

    @NotBlank(message = "Training name is required")
    private final String trainingName;

    @NotBlank(message = "Training type name is required")
    private final String trainingTypeName;

    @NotNull(message = "Training date is required")
    private final LocalDate trainingDate;

    @NotNull(message = "Training duration is required")
    private final int trainingDuration;
}
