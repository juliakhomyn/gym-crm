package com.gym.crm.dto.trainer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class TrainerUpdateDTO {
    @NotNull(message = "Id is required")
    @Positive(message = "Id must be a positive number")
    private Long id;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private final String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    private final String lastName;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 110, message = "Username must be between 5 and 110 characters long")
    private final String username;

    @NotBlank(message = "Password is required")
    @Size(min = 10, max = 100, message = "Password must be between 10 and 100 characters long")
    @ToString.Exclude
    private final String password;

    @NotBlank(message = "Specialization is required")
    @Size(max = 100, message = "Specialization cannot exceed 100 characters")
    private final String specialization;

    @NotNull(message = "Is active is required")
    private final Boolean isActive;
}
