package com.gym.crm.csv;

import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CsvParserTest {

    private CsvParser csvParser;

    @BeforeEach
    void setUp() {
        csvParser = new CsvParser();
    }

    @Test
    void parseTrainee_shouldBuildTraineeFromValidCsvLine() {
        String line = "1,Callum,Whitfield,Callum.Whitfield,password123,true,2000-01-01,123 Main St";

        Trainee actual = csvParser.parseTrainee(line);

        assertEquals(1L, actual.getUserId());
        assertEquals("Callum", actual.getFirstName());
        assertEquals("Whitfield", actual.getLastName());
        assertEquals("Callum.Whitfield", actual.getUsername());
        assertEquals("password123", actual.getPassword());
        assertTrue(actual.getIsActive());
        assertEquals(LocalDate.of(2000, 1, 1), actual.getDateOfBirth());
        assertEquals("123 Main St", actual.getAddress());
    }

    @Test
    void parseTrainee_shouldParseTraineeWithIsActiveFalse() {
        String line = "2,Nora,Pemberton,Nora.Pemberton,pass456,false,1999-11-30,456 Oak Ave";

        Trainee actual = csvParser.parseTrainee(line);

        assertFalse(actual.getIsActive());
    }

    @Test
    void parseTrainee_shouldTrimWhitespace() {
        String line = " 1 , Callum , Whitfield , Callum.Whitfield , password , true , 2000-01-01 , 123 Main St ";

        Trainee actual = csvParser.parseTrainee(line);

        assertEquals("Callum", actual.getFirstName());
        assertEquals("Whitfield", actual.getLastName());
    }

    @Test
    void parseTrainer_shouldBuildTrainerFromValidCsvLine() {
        String line = "1,Callum,Whitfield,Callum.Whitfield,password123,true,Cardio";

        Trainer actual = csvParser.parseTrainer(line);

        assertEquals(1L, actual.getUserId());
        assertEquals("Callum", actual.getFirstName());
        assertEquals("Whitfield", actual.getLastName());
        assertEquals("Callum.Whitfield", actual.getUsername());
        assertEquals("password123", actual.getPassword());
        assertTrue(actual.getIsActive());
        assertEquals("Cardio", actual.getSpecialization().getTrainingTypeName());
    }

    @Test
    void parseTrainer_shouldTrimWhitespace() {
        String line = " 1 , Callum , Whitfield , Callum.Whitfield , password , true , Cardio ";

        Trainer actual = csvParser.parseTrainer(line);

        assertEquals("Cardio", actual.getSpecialization().getTrainingTypeName());
    }

    @Test
    void parseTraining_shouldBuildTrainingFromValidCsvLine() {
        String line = "1,1,2,Morning Cardio,Cardio,2024-01-15,60";

        Training actual = csvParser.parseTraining(line);

        assertEquals(1L, actual.getId());
        assertEquals(1L, actual.getTraineeId());
        assertEquals(2L, actual.getTrainerId());
        assertEquals("Morning Cardio", actual.getTrainingName());
        assertEquals("Cardio", actual.getTrainingType().getTrainingTypeName());
        assertEquals(LocalDate.of(2024, 1, 15), actual.getTrainingDate());
        assertEquals(60, actual.getTrainingDuration());
    }

    @Test
    void parseTraining_shouldTrimWhitespace() {
        String line = " 1 , 1 , 2 , Morning Cardio , Cardio , 2024-01-15 , 60 ";

        Training actual = csvParser.parseTraining(line);

        assertEquals("Morning Cardio", actual.getTrainingName());
        assertEquals(60, actual.getTrainingDuration());
    }
}
