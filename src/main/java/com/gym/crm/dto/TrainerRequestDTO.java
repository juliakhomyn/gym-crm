package com.gym.crm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class TrainerRequestDTO {
    @NotBlank(message = "First name is required")
    private final String firstName;

    @NotBlank(message = "Last name is required")
    private final String lastName;

    @NotBlank(message = "Specialization is required")
    private final String specialization;
}
