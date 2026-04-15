package com.gym.crm.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.Builder;

import java.time.LocalDate;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class Training {
    private final Long traineeId;
    private final Long trainerId;
    private final String name;
    private final TrainingType type;
    private final LocalDate date;
    private final int duration;
}
