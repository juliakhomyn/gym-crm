package com.gym.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class TraineeRequestDTO {
    @NotBlank(message = "First name is required")
    private final String firstName;

    @NotBlank(message = "Last name is required")
    private final String lastName;

    @Past(message = "Date of birth must be in the past")
    private final LocalDate dateOfBirth;

    private final String address;
}
