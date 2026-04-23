package com.gym.crm.storage;

import com.gym.crm.csv.CsvParser;
import com.gym.crm.csv.CsvReader;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.enums.StorageNamespace;
import jakarta.annotation.PostConstruct;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
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
            log.warn("Failed to load from files");
            throw new RuntimeException(e);
        }
    }

    private void loadTrainees() throws IOException, IllegalArgumentException {
        log.info("Loading trainees from: {}", traineeFilePath);

        Map<Long, Trainee> storage = inMemoryStorage.getStorage(StorageNamespace.TRAINEE);
        csvReader.readCsv(traineeFilePath, true)
                .stream()
                .map(line -> csvParser.parseTrainee(line))
                .forEach(trainee -> storage.put(trainee.getUserId(), trainee));

        log.info("Loaded {} trainees successfully", storage.size());
    }

    private void loadTrainers() throws IOException, IllegalArgumentException {
        log.info("Loading trainers from: {}", trainerFilePath);

        Map<Long, Trainer> storage = inMemoryStorage.getStorage(StorageNamespace.TRAINER);
        csvReader.readCsv(trainerFilePath, true)
                .stream()
                .map(line -> csvParser.parseTrainer(line))
                .forEach(trainer -> storage.put(trainer.getUserId(), trainer));

        log.info("Loaded {} trainers successfully", storage.size());
    }

    private void loadTrainings() throws IOException, IllegalArgumentException {
        log.info("Loading trainings from: {}", trainingFilePath);

        Map<Long, Training> storage = inMemoryStorage.getStorage(StorageNamespace.TRAINING);
        csvReader.readCsv(trainingFilePath, true)
                .stream()
                .map(line -> csvParser.parseTraining(line))
                .forEach(training -> storage.put(training.getId(), training));

        log.info("Loaded {} trainings successfully", storage.size());
    }
}
