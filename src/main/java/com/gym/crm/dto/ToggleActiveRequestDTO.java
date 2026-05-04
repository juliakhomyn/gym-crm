package com.gym.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class ToggleActiveRequestDTO {
    @NotBlank(message = "Username is required")
    private final String username;

    @NotNull(message = "Is active is required")
    private final boolean isActive;
}
