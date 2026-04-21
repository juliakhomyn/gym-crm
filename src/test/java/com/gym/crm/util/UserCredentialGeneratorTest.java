package com.gym.crm.util;

import com.gym.crm.dao.TraineeDAO;
import com.gym.crm.dao.TrainerDAO;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserCredentialGeneratorTest {
    private static final String FIRST_NAME = "Cillian";
    private static final String LAST_NAME = "Mercer";
    private static final String USERNAME = "Cillian.Mercer";
    private static final String USERNAME_WITH_SUFFIX_1 = "Cillian.Mercer1";
    private static final String USERNAME_WITH_SUFFIX_2 = "Cillian.Mercer2";
    private static final int PASSWORD_LENGTH = 10;

    @Mock
    private TraineeDAO traineeDAO;
    @Mock
    private TrainerDAO trainerDAO;

    @InjectMocks
    private UserCredentialGenerator userCredentialGenerator;

    @Test
    public void generateUsername_shouldReturnConcatenatedUsername_whenNoDuplicates() {
        when(traineeDAO.findAll()).thenReturn(List.of());
        when(trainerDAO.findAll()).thenReturn(List.of());

        String username = userCredentialGenerator.generateUsername(FIRST_NAME, LAST_NAME);

        assertEquals(USERNAME, username);
    }

    @Test
    public void generateUsername_shouldReturnUsernameWithSuffix_whenDuplicateExists() {
        Trainee existingTrainee = Trainee.builder()
                .username(USERNAME)
                .build();

        when(traineeDAO.findAll()).thenReturn(List.of(existingTrainee));
        when(trainerDAO.findAll()).thenReturn(List.of());

        String username = userCredentialGenerator.generateUsername(FIRST_NAME, LAST_NAME);

        assertEquals(USERNAME_WITH_SUFFIX_1, username);
    }

    @Test
    public void generateUsername_shouldReturnUsernameWithSuffix2_whenTwoDuplicatesExist() {
        Trainee existingTrainee = Trainee.builder()
                .username(USERNAME)
                .build();
        Trainer existingTrainer = Trainer.builder()
                .username(USERNAME_WITH_SUFFIX_1)
                .build();

        when(traineeDAO.findAll()).thenReturn(List.of(existingTrainee));
        when(trainerDAO.findAll()).thenReturn(List.of(existingTrainer));

        String username = userCredentialGenerator.generateUsername(FIRST_NAME, LAST_NAME);

        assertEquals(USERNAME_WITH_SUFFIX_2, username);
    }

    @Test
    public void generatePassword_shouldReturnStringOfLength10() {
        String password = userCredentialGenerator.generatePassword();

        assertEquals(PASSWORD_LENGTH, password.length());
    }

    @Test
    public void generatePassword_shouldReturnDifferentPasswords_whenCalledTwoTimes() {
        String password1 = userCredentialGenerator.generatePassword();
        String password2 = userCredentialGenerator.generatePassword();

        assertNotEquals(password1, password2);
    }

}
