package com.gym.crm.storage;

import com.gym.crm.csv.CsvParser;
import com.gym.crm.csv.CsvReader;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import com.gym.crm.model.enums.StorageNamespace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StorageInitializerTest {
    private static final String TRAINEE_FILE_NAME = "trainee.csv";
    private static final String TRAINER_FILE_NAME = "trainer.csv";
    private static final String TRAINING_FILE_NAME = "training.csv";

    @Mock
    private InMemoryStorage storage;
    @Mock
    private CsvReader csvReader;
    @Mock
    private CsvParser csvParser;

    @InjectMocks
    private StorageInitializer storageInitializer;

    private Map<Long, Trainee> traineeStorage;
    private Map<Long, Trainer> trainerStorage;
    private Map<Long, Training> trainingStorage;

    private Trainee trainee;
    private Trainer trainer;
    private Training training;

    @BeforeEach
    void setUp() {
        traineeStorage = new HashMap<>();
        trainerStorage = new HashMap<>();
        trainingStorage = new HashMap<>();

        storageInitializer.setTraineeFilePath(TRAINEE_FILE_NAME);
        storageInitializer.setTrainerFilePath(TRAINER_FILE_NAME);
        storageInitializer.setTrainingFilePath(TRAINING_FILE_NAME);

        trainee = buildTrainee();
        trainer = buildTrainer();
        training = buildTraining();
    }

    @Test
    void init_shouldLoadAllEntities() throws IOException {
        stubAllStorages();
        when(csvReader.readCsv(TRAINEE_FILE_NAME, true)).thenReturn(List.of("1,Declan,Ashford,Declan.Ashford,password,true,2000-01-01,123 Main St"));
        when(csvReader.readCsv(TRAINER_FILE_NAME, true)).thenReturn(List.of("1,Ingrid,Holloway,Ingrid.Holloway,password,true,Cardio"));
        when(csvReader.readCsv(TRAINING_FILE_NAME, true)).thenReturn(List.of("1,1,1,Morning Cardio,Cardio,2024-01-15,60"));
        when(csvParser.parseTrainee(any())).thenReturn(trainee);
        when(csvParser.parseTrainer(any())).thenReturn(trainer);
        when(csvParser.parseTraining(any())).thenReturn(training);

        storageInitializer.init();

        assertEquals(1, traineeStorage.size());
        assertEquals(1, trainerStorage.size());
        assertEquals(1, trainingStorage.size());
    }

    @Test
    void shouldLoadTraineesIntoCorrectNamespace() throws IOException {
        stubAllStorages();
        when(csvReader.readCsv(TRAINEE_FILE_NAME, true)).thenReturn(List.of("1,Declan,Ashford,Declan.Ashford,password,true,2000-01-01,123 Main St"));
        when(csvReader.readCsv(TRAINER_FILE_NAME, true)).thenReturn(List.of());
        when(csvReader.readCsv(TRAINING_FILE_NAME, true)).thenReturn(List.of());
        when(csvParser.parseTrainee(any())).thenReturn(trainee);

        storageInitializer.init();

        assertEquals(trainee, traineeStorage.get(1L));
        assertTrue(trainerStorage.isEmpty());
        assertTrue(trainingStorage.isEmpty());
    }

    @Test
    void shouldThrowRuntimeExceptionWhenFileNotFound() throws IOException {
        when(csvReader.readCsv(TRAINEE_FILE_NAME, true))
                .thenThrow(new FileNotFoundException(TRAINEE_FILE_NAME));

        assertThrows(RuntimeException.class, () -> storageInitializer.init());
    }

    @Test
    void shouldThrowRuntimeExceptionWhenCsvLineIsInvalid() throws IOException {
        when(csvReader.readCsv(TRAINEE_FILE_NAME, true)).thenReturn(List.of("invalid,data"));
        when(csvParser.parseTrainee("invalid,data"))
                .thenThrow(new IllegalArgumentException("Failed to parse line"));

        assertThrows(RuntimeException.class, () -> storageInitializer.init());
    }

    @Test
    void shouldHandleEmptyFiles() throws IOException {
        stubAllEmptyCsvFiles();

        storageInitializer.init();

        assertTrue(traineeStorage.isEmpty());
        assertTrue(trainerStorage.isEmpty());
        assertTrue(trainingStorage.isEmpty());
    }

    private void stubAllStorages() throws IOException {
        when(storage.getStorage(StorageNamespace.TRAINEE)).thenReturn((Map) traineeStorage);
        when(storage.getStorage(StorageNamespace.TRAINER)).thenReturn((Map) trainerStorage);
        when(storage.getStorage(StorageNamespace.TRAINING)).thenReturn((Map) trainingStorage);
    }

    private void stubAllEmptyCsvFiles() throws IOException {
        when(csvReader.readCsv(TRAINEE_FILE_NAME, true)).thenReturn(List.of());
        when(csvReader.readCsv(TRAINER_FILE_NAME, true)).thenReturn(List.of());
        when(csvReader.readCsv(TRAINING_FILE_NAME, true)).thenReturn(List.of());
    }
    
    private Trainee buildTrainee() {
        return Trainee.builder()
                .userId(1L)
                .firstName("Declan")
                .lastName("Ashford")
                .username("Declan.Ashford")
                .password("password")
                .isActive(true)
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("123 Main St")
                .build();
    }
    
    private Trainer buildTrainer() {
        return Trainer.builder()
                .userId(1L)
                .firstName("Ingrid")
                .lastName("Holloway")
                .username("Ingrid.Holloway")
                .password("password")
                .isActive(true)
                .specialization(new TrainingType("Cardio"))
                .build();
    }
    
    private Training buildTraining() {
        return Training.builder()
                .id(1L)
                .traineeId(1L)
                .trainerId(1L)
                .trainingName("Morning Cardio")
                .trainingType(new TrainingType("Cardio"))
                .trainingDate(LocalDate.of(2024, 1, 15))
                .trainingDuration(60)
                .build();
    }
}
