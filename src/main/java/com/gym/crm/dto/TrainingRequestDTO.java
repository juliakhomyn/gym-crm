package com.gym.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
    @NotNull(message = "Trainee id is required")
    @Positive(message = "Trainee id must be a positive number")
    private final Long traineeId;

    @NotNull(message = "Trainer id is required")
    @Positive(message = "Trainer id must be a positive number")
    private final Long trainerId;

    @NotBlank(message = "Training name is required")
    @Size(max = 100, message = "Training name cannot exceed 100 characters")
    private final String trainingName;

    @NotBlank(message = "Training type name is required")
    @Size(max = 100, message = "Training type name cannot exceed 100 characters")
    private final String trainingTypeName;

    @NotNull(message = "Training date is required")
    private final LocalDate trainingDate;

    @NotNull(message = "Training duration is required")
    @Positive(message = "Training duration must be a positive number")
    private final int trainingDuration;
}
