package com.gym.crm.search.filter;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public class TraineeTrainingFilter extends TrainingFilter {
    private String trainingTypeName;
}
