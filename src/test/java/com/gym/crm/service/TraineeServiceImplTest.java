package com.gym.crm.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.gym.crm.dao.TraineeDAO;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.model.Trainee;
import com.gym.crm.service.impl.TraineeServiceImpl;
import com.gym.crm.util.UserCredentialGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TraineeServiceImplTest {
    private static final String FIRST_NAME = "Owen";
    private static final String LAST_NAME = "Castleberry";
    private static final String USERNAME = "Owen.Castleberry";
    private static final String ENCODED_PASSWORD = "encodedPassword";
    private static final String RAW_PASSWORD = "rawPassword";
    private static final long VALID_ID = 1L;
    private static final long NOT_FOUND_ID = 999L;

    private static final String TRAINEE_CANNOT_BE_NULL = "Trainee cannot be null";
    private static final String TRAINEE_NOT_FOUND_BY_ID = "Trainee not found by id: %s";

    @Mock
    private TraineeDAO dao;
    @Mock
    private UserCredentialGenerator userCredentialGenerator;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TraineeServiceImpl service;

    private Trainee trainee;
    private Trainee savedTrainee;
    private ListAppender<ILoggingEvent> logAppender;

    @BeforeEach
    void setUp() {
        trainee = buildTrainee();

        savedTrainee = trainee.toBuilder()
                .userId(VALID_ID)
                .username(USERNAME)
                .password(ENCODED_PASSWORD)
                .isActive(true)
                .build();

        Logger logger = (Logger) LoggerFactory.getLogger(TraineeServiceImpl.class);
        logAppender = new ListAppender<>();
        logAppender.start();
        logger.addAppender(logAppender);
    }

    @AfterEach
    void tearDown() {
        Logger logger = (Logger) LoggerFactory.getLogger(TraineeServiceImpl.class);
        logger.detachAppender(logAppender);
    }

    @Test
    void createTrainee_shouldSaveTraineeWithCredentials() {
        when(userCredentialGenerator.generateUsername(FIRST_NAME, LAST_NAME)).thenReturn(USERNAME);
        when(userCredentialGenerator.generatePassword()).thenReturn(RAW_PASSWORD);
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(dao.save(any(Trainee.class))).thenReturn(savedTrainee);

        Trainee actual = service.createTrainee(trainee);

        assertEquals(USERNAME, actual.getUsername());
        assertEquals(ENCODED_PASSWORD, actual.getPassword());
        assertTrue(actual.getIsActive());
        verify(dao).save(any(Trainee.class));
    }

    @Test
    void createTrainee_shouldThrowException_whenTraineeIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.createTrainee(null));

        assertEquals(TRAINEE_CANNOT_BE_NULL, exception.getMessage());
    }

    @Test
    void updateTrainee_shouldUpdateTrainee_whenTraineeExists() {
        Trainee expected = trainee.toBuilder()
                .address("new address")
                .build();

        when(dao.findById(VALID_ID)).thenReturn(Optional.of(savedTrainee));
        when(dao.update(savedTrainee)).thenReturn(expected);

        Trainee actual = service.updateTrainee(savedTrainee);

        assertEquals(expected, actual);
        verify(dao).update(savedTrainee);
    }

    @Test
    void updateTrainee_shouldThrowException_whenTraineeIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.updateTrainee(null));

        assertEquals(TRAINEE_CANNOT_BE_NULL, exception.getMessage());
    }

    @Test
    void updateTrainee_shouldThrowException_whenTraineeNotFound() {
        Trainee nonExistent = savedTrainee.toBuilder().userId(NOT_FOUND_ID).build();
        when(dao.findById(NOT_FOUND_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> service.updateTrainee(nonExistent));

        assertEquals(String.format(TRAINEE_NOT_FOUND_BY_ID, NOT_FOUND_ID), exception.getMessage());
        verify(dao, never()).update(any(Trainee.class));
    }

    @Test
    void deleteTrainee_shouldDeleteTrainee_whenTraineeExists() {
        when(dao.findById(VALID_ID)).thenReturn(Optional.of(savedTrainee));

        service.deleteTrainee(VALID_ID);

        verify(dao).delete(VALID_ID);
    }

    @Test
    void deleteTrainee_shouldThrowException_whenTraineeNotFound() {
        when(dao.findById(NOT_FOUND_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> service.deleteTrainee(NOT_FOUND_ID));
        verify(dao, never()).delete(any());
    }

    @Test
    void getTraineeById_shouldReturnTrainee_whenTraineeExists() {
        when(dao.findById(VALID_ID)).thenReturn(Optional.of(savedTrainee));

        Trainee actual = service.getTraineeById(VALID_ID);

        assertEquals(savedTrainee, actual);
    }

    @Test
    void findById_shouldThrowException_whenTraineeNotFound() {
        when(dao.findById(NOT_FOUND_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> service.getTraineeById(NOT_FOUND_ID));
        assertEquals(String.format(TRAINEE_NOT_FOUND_BY_ID, NOT_FOUND_ID), exception.getMessage());
    }

    @Test
    void getAllTrainees_shouldReturnAllTrainees_whenExist() {
        when(dao.findAll()).thenReturn(List.of(savedTrainee));

        List<Trainee> actual = service.getAllTrainees();

        assertEquals(1, actual.size());
    }

    @Test
    void getAllTrainees_shouldReturnEmptyList_whenNoTrainees() {
        when(dao.findAll()).thenReturn(List.of());

        List<Trainee> actual = service.getAllTrainees();

        assertTrue(actual.isEmpty());
    }

    @Test
    void createTrainee_shouldLogInfo_whenCreatingTrainee() {
        when(userCredentialGenerator.generateUsername(FIRST_NAME, LAST_NAME)).thenReturn(USERNAME);
        when(userCredentialGenerator.generatePassword()).thenReturn(RAW_PASSWORD);
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(dao.save(any(Trainee.class))).thenReturn(savedTrainee);

        service.createTrainee(trainee);

        List<ILoggingEvent> infoLogs = logAppender.list.stream()
                .filter(log -> log.getLevel() == Level.INFO)
                .toList();

        assertTrue(infoLogs.get(0).getFormattedMessage().contains(FIRST_NAME));
        assertTrue(infoLogs.get(0).getFormattedMessage().contains(LAST_NAME));
        assertTrue(infoLogs.get(1).getFormattedMessage().contains(USERNAME));
    }

    private Trainee buildTrainee() {
        return Trainee.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("123 Main St")
                .build();
    }
}
