package com.gym.crm.csv;

import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class CsvParser {
    private static final String SEPARATOR = ",";

    public Trainee parseTrainee(String line) {
        String[] fields = line.split(SEPARATOR);
        return Trainee.builder()
                .userId(Long.valueOf(fields[0].trim()))
                .firstName(fields[1].trim())
                .lastName(fields[2].trim())
                .username(fields[3].trim())
                .password(fields[4].trim())
                .isActive(Boolean.parseBoolean(fields[5].trim()))
                .dateOfBirth(LocalDate.parse(fields[6].trim()))
                .address(fields[7].trim())
                .build();
    }

    public Trainer parseTrainer(String line) {
        String[] fields = line.split(SEPARATOR);
        return Trainer.builder()
                .userId(Long.valueOf(fields[0].trim()))
                .firstName(fields[1].trim())
                .lastName(fields[2].trim())
                .username(fields[3].trim())
                .password(fields[4].trim())
                .isActive(Boolean.parseBoolean(fields[5].trim()))
                .specialization(new TrainingType(fields[6].trim()))
                .build();
    }

    public Training parseTraining(String line) {
        String[] fields = line.split(SEPARATOR);
        return Training.builder()
                .id(Long.valueOf(fields[0].trim()))
                .traineeId(Long.valueOf(fields[1].trim()))
                .trainerId(Long.valueOf(fields[2].trim()))
                .trainingName(fields[3].trim())
                .trainingType(new TrainingType(fields[4].trim()))
                .trainingDate(LocalDate.parse(fields[5].trim()))
                .trainingDuration(Integer.parseInt(fields[6].trim()))
                .build();
    }
}
