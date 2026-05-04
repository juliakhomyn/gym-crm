package com.gym.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotBlank(message = "First name is required")
    private final String firstName;

    @NotBlank(message = "Last name is required")
    private final String lastName;

    @NotBlank(message = "Username is required")
    private final String username;

    @NotBlank(message = "Password is required")
    @Size(min = 10, message = "Password must contain minimum 10 characters")
    @ToString.Exclude
    private final String password;

    @NotBlank(message = "Specialization is required")
    private final String specialization;

    @NotNull(message = "Is active is required")
    private final Boolean isActive;
}
