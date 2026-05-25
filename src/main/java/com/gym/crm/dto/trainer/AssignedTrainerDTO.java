package com.gym.crm.dto.trainer;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class AssignedTrainerDTO {
    private final String username;
    private final String firstName;
    private final String lastName;
    private final String specialization;
}
