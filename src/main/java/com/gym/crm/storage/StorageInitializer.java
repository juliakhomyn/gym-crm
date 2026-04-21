package com.gym.crm.storage;

import com.gym.crm.csv.CsvParser;
import com.gym.crm.csv.CsvReader;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.enums.StorageNamespace;
import jakarta.annotation.PostConstruct;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class StorageInitializer {

    @Setter(onMethod_={@Autowired})
    private InMemoryStorage inMemoryStorage;

    @Setter(onMethod_={@Autowired})
    private CsvReader csvReader;

    @Setter(onMethod_={@Autowired})
    private CsvParser csvParser;

    @Setter
    @Value("${storage.data.trainee}")
    private String traineeFilePath;

    @Setter
    @Value("${storage.data.trainer}")
    private String trainerFilePath;

    @Setter
    @Value("${storage.data.training}")
    private String trainingFilePath;

    @PostConstruct
    public void init() {
        try {
            loadTrainees();
            loadTrainers();
            loadTrainings();
        } catch (IllegalArgumentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadTrainees() throws IOException, IllegalArgumentException {
        Map<Long, Trainee> storage = inMemoryStorage.getStorage(StorageNamespace.TRAINEE);
        csvReader.readCsv(traineeFilePath, true)
                .stream()
                .map(line -> csvParser.parseTrainee(line))
                .forEach(trainee -> storage.put(trainee.getUserId(), trainee));
    }

    private void loadTrainers() throws IOException, IllegalArgumentException {
        Map<Long, Trainer> storage = inMemoryStorage.getStorage(StorageNamespace.TRAINER);
        csvReader.readCsv(trainerFilePath, true)
                .stream()
                .map(line -> csvParser.parseTrainer(line))
                .forEach(trainer -> storage.put(trainer.getUserId(), trainer));
    }

    private void loadTrainings() throws IOException, IllegalArgumentException {
        Map<Long, Training> storage = inMemoryStorage.getStorage(StorageNamespace.TRAINING);
        csvReader.readCsv(trainingFilePath, true)
                .stream()
                .map(line -> csvParser.parseTraining(line))
                .forEach(training -> storage.put(training.getId(), training));
    }
}
