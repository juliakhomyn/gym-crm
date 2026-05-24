package com.gym.crm.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.gym.crm.dao.TraineeDAO;
import com.gym.crm.dao.TrainerDAO;
import com.gym.crm.dto.trainee.TraineeInfoDTO;
import com.gym.crm.dto.trainee.TraineeRequestDTO;
import com.gym.crm.dto.trainee.TraineeResponseDTO;
import com.gym.crm.dto.trainee.TraineeUpdateDTO;
import com.gym.crm.dto.trainee.TrainerAssignmentUpdateDTO;
import com.gym.crm.dto.trainer.TrainerInfoDTO;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.exception.ValidationFailedException;
import com.gym.crm.mapper.TraineeMapper;
import com.gym.crm.mapper.TrainerMapper;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.service.common.UserInputValidator;
import com.gym.crm.service.common.UserProfileService;
import com.gym.crm.service.impl.TraineeServiceImpl;
import com.gym.crm.testutils.TestDataProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {
    private static final String FIRST_NAME = "Simone";
    private static final String LAST_NAME = "Radcliffe";
    private static final String USERNAME = "Simone.Radcliffe";
    private static final String TRAINER_USERNAME1 = "trainer1";
    private static final String TRAINER_USERNAME2 = "trainer2";
    private static final String NOT_FOUND_USERNAME = "Not.Found";
    private static final String BLANK_USERNAME = " ";
    private static final String ENCODED_PASSWORD = "encodedPassword";
    private static final String RAW_PASSWORD = "rawPassword";
    private static final long VALID_ID = 1L;
    private static final long VALID_ID1 = 2L;
    private static final long INVALID_ID = -1L;
    private static final long NOT_FOUND_ID = 999L;

    private static final String TRAINEE_CANNOT_BE_NULL = "Trainee cannot be null";
    private static final String TRAINEE_NOT_FOUND_BY_ID = "Trainee not found by id: %s";
    private static final String TRAINEE_NOT_FOUND_BY_USERNAME = "Trainee not found by username: %s";
    private static final String ID_CANNOT_BE_NULL = "ID cannot be null";
    private static final String ID_CANNOT_BE_NEGATIVE = "ID must be a positive number";
    private static final String USERNAME_CANNOT_BE_NULL = "Username cannot be null or empty";
    private static final String USER_REGISTERED_AS_TRAINER = "User with username %s is already registered as a trainer";

    private final Trainee trainee = TestDataProvider.buildTrainee();
    private final Trainee savedTrainee = TestDataProvider.buildSavedTrainee();
    private final TraineeRequestDTO request = TestDataProvider.buildTraineeRequestDTO();
    private final TraineeResponseDTO response = TestDataProvider.buildTraineeResponseDTO();
    private final TraineeInfoDTO info = TestDataProvider.buildTraineeInfoDTO();
    private final TrainerAssignmentUpdateDTO trainerAssignmentUpdateDTO = TestDataProvider.buildValidTrainerAssignmentUpdateDto();

    @Mock
    private TraineeDAO dao;
    @Mock
    private UserProfileService userProfileService;
    @Mock
    private TraineeMapper mapper;
    @Mock
    private UserInputValidator userInputValidator;
    @Mock
    private TrainerDAO trainerDAO;
    @Mock
    private TrainerMapper trainerMapper;

    @InjectMocks
    private TraineeServiceImpl service;

    private ListAppender<ILoggingEvent> logAppender;

    @BeforeEach
    void setUp() {
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
        TraineeResponseDTO expected = response.toBuilder().password(RAW_PASSWORD).build();

        when(mapper.toEntity(request)).thenReturn(trainee);
        when(userProfileService.generateUsername(FIRST_NAME, LAST_NAME)).thenReturn(USERNAME);
        when(userProfileService.generatePassword()).thenReturn(RAW_PASSWORD);
        when(userProfileService.encodePassword(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(dao.save(any(Trainee.class))).thenReturn(savedTrainee);
        when(mapper.toDto(savedTrainee)).thenReturn(response);

        TraineeResponseDTO actual = service.createTrainee(request);

        assertThat(actual).isEqualTo(expected);
        verify(userInputValidator).validate(request, "Trainee");
        verify(mapper).toEntity(request);
        verify(userProfileService).generateUsername(FIRST_NAME, LAST_NAME);
        verify(userProfileService).generatePassword();
        verify(userProfileService).encodePassword(RAW_PASSWORD);
        verify(dao).save(any(Trainee.class));
        verify(mapper).toDto(savedTrainee);
    }

    @Test
    void createTrainee_shouldThrowException_whenTraineeIsNull() {
        doThrow(new ValidationFailedException(TRAINEE_CANNOT_BE_NULL)).when(userInputValidator).validate(null, "Trainee");

        ValidationFailedException exception = assertThrows(ValidationFailedException.class, () -> service.createTrainee(null));

        assertThat(exception.getMessage()).isEqualTo(TRAINEE_CANNOT_BE_NULL);
    }

    @Test
    void createTrainee_shouldThrowValidationFailedException_ifTrainerExists() {
        when(mapper.toEntity(request)).thenReturn(trainee);
        when(userProfileService.generateUsername(FIRST_NAME, LAST_NAME)).thenReturn(USERNAME);
        when(userProfileService.generatePassword()).thenReturn(RAW_PASSWORD);
        when(trainerDAO.findByUsername(USERNAME)).thenReturn(Optional.of(new Trainer()));

        ValidationFailedException exception = assertThrows(ValidationFailedException.class, () -> service.createTrainee(request));

        assertThat(exception.getMessage()).isEqualTo(String.format(USER_REGISTERED_AS_TRAINER, USERNAME));
        verify(dao, never()).save(any());
    }

    @Test
    void updateTrainee_shouldUpdateTrainee_whenTraineeExists() {
        TraineeUpdateDTO updateDTO = TestDataProvider.buildTraineeUpdateDTO();

        when(dao.findByUsername(USERNAME)).thenReturn(Optional.ofNullable(savedTrainee));
        when(dao.update(any(Trainee.class))).thenReturn(savedTrainee);
        when(mapper.toDto(savedTrainee)).thenReturn(response);

        TraineeResponseDTO actual = service.updateTrainee(updateDTO);

        assertThat(actual).isEqualTo(response);
        verify(dao).update(any(Trainee.class));
        verify(mapper).toDto(savedTrainee);
    }

    @Test
    void updateTrainee_shouldThrowException_whenTraineeIsNull() {
        doThrow(new ValidationFailedException(TRAINEE_CANNOT_BE_NULL)).when(userInputValidator).validate(null, "Trainee");

        ValidationFailedException exception = assertThrows(ValidationFailedException.class, () -> service.updateTrainee(null));

        assertThat(exception.getMessage()).isEqualTo(TRAINEE_CANNOT_BE_NULL);
    }

    @Test
    void updateTrainee_shouldThrowException_whenTraineeNotFound() {
        TraineeUpdateDTO nonExistent = TestDataProvider.buildNonExistentTraineeUpdateDTO();
        when(dao.findByUsername(NOT_FOUND_USERNAME)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.updateTrainee(nonExistent));

        assertThat(exception.getMessage()).isEqualTo(String.format(TRAINEE_NOT_FOUND_BY_USERNAME, NOT_FOUND_USERNAME));
        verify(dao, never()).update(any(Trainee.class));
    }

    @Test
    void deleteTraineeById_shouldDeleteTrainee_whenTraineeExists() {
        when(dao.findById(VALID_ID)).thenReturn(Optional.of(savedTrainee));

        service.deleteTraineeById(VALID_ID);

        verify(dao).delete(VALID_ID);
    }

    @Test
    void deleteTraineeById_shouldThrowException_whenTraineeNotFound() {
        when(dao.findById(NOT_FOUND_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.deleteTraineeById(NOT_FOUND_ID));

        assertThat(exception.getMessage()).isEqualTo(String.format(TRAINEE_NOT_FOUND_BY_ID, NOT_FOUND_ID));
        verify(dao, never()).delete(any());
    }

    @Test
    void deleteTraineeById_shouldThrow_whenIdIsNull() {
        doThrow(new ValidationFailedException(ID_CANNOT_BE_NULL)).when(userInputValidator).validateId(null);

        ValidationFailedException exception = assertThrows(ValidationFailedException.class, () -> service.deleteTraineeById(null));

        assertThat(exception.getMessage()).isEqualTo(ID_CANNOT_BE_NULL);
        verify(dao, never()).delete(any());
    }

    @Test
    void deleteTraineeById_shouldThrow_whenIdIsInvalid() {
        doThrow(new ValidationFailedException(ID_CANNOT_BE_NEGATIVE)).when(userInputValidator).validateId(INVALID_ID);

        ValidationFailedException exception = assertThrows(ValidationFailedException.class, () -> service.deleteTraineeById(INVALID_ID));

        assertThat(exception.getMessage()).isEqualTo(ID_CANNOT_BE_NEGATIVE);
        verify(dao, never()).delete(any());
    }

    @Test
    void deleteByUsername_shouldDelete_whenTraineeExists() {
        when(dao.findByUsername(USERNAME)).thenReturn(Optional.of(trainee));

        service.deleteByUsername(USERNAME);

        verify(dao).deleteByUsername(USERNAME);
    }

    @Test
    void deleteByUsername_shouldThrow_whenUsernameIsNull() {
        doThrow(new ValidationFailedException(USERNAME_CANNOT_BE_NULL)).when(userInputValidator).validateUsername(null);

        ValidationFailedException exception = assertThrows(ValidationFailedException.class, () -> service.deleteByUsername(null));

        assertThat(exception.getMessage()).isEqualTo(USERNAME_CANNOT_BE_NULL);
        verify(dao, never()).deleteByUsername(any());
    }

    @Test
    void deleteByUsername_shouldThrow_whenUsernameIsBlank() {
        doThrow(new ValidationFailedException(USERNAME_CANNOT_BE_NULL)).when(userInputValidator).validateUsername(BLANK_USERNAME);

        ValidationFailedException exception = assertThrows(ValidationFailedException.class, () -> service.deleteByUsername(BLANK_USERNAME));

        assertThat(exception.getMessage()).isEqualTo(USERNAME_CANNOT_BE_NULL);
        verify(dao, never()).deleteByUsername(any());
    }

    @Test
    void deleteByUsername_shouldThrow_whenTraineeNotFound() {
        when(dao.findByUsername(USERNAME)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.deleteByUsername(USERNAME));

        assertThat(exception.getMessage()).isEqualTo(String.format(TRAINEE_NOT_FOUND_BY_USERNAME, USERNAME));
        verify(dao, never()).deleteByUsername(any());
    }

    @Test
    void getTraineeById_shouldReturnTrainee_whenTraineeExists() {
        when(dao.findById(VALID_ID)).thenReturn(Optional.of(savedTrainee));
        when(mapper.toInfoDto(savedTrainee)).thenReturn(info);

        TraineeInfoDTO actual = service.getTraineeById(VALID_ID);

        assertThat(actual).isEqualTo(info);
    }

    @Test
    void gerTraineeById_shouldThrowException_whenTraineeNotFound() {
        when(dao.findById(NOT_FOUND_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.getTraineeById(NOT_FOUND_ID));

        assertThat(exception.getMessage()).isEqualTo(String.format(TRAINEE_NOT_FOUND_BY_ID, NOT_FOUND_ID));
    }

    @Test
    void getTraineeById_shouldThrow_whenIdIsNull() {
        doThrow(new ValidationFailedException(ID_CANNOT_BE_NULL)).when(userInputValidator).validateId(null);

        ValidationFailedException exception = assertThrows(ValidationFailedException.class, () -> service.getTraineeById(null));

        assertThat(exception.getMessage()).isEqualTo(ID_CANNOT_BE_NULL);
    }

    @Test
    void getTraineeById_shouldThrow_whenIdIsNegative() {
        doThrow(new ValidationFailedException(ID_CANNOT_BE_NEGATIVE)).when(userInputValidator).validateId(INVALID_ID);

        ValidationFailedException exception = assertThrows(ValidationFailedException.class, () -> service.getTraineeById(INVALID_ID));

        assertThat(exception.getMessage()).isEqualTo(ID_CANNOT_BE_NEGATIVE);
    }

    @Test
    void getTraineeByUsername_shouldReturnTrainee_whenExists() {
        when(dao.findByUsername(USERNAME)).thenReturn(Optional.of(trainee));
        when(mapper.toInfoDto(trainee)).thenReturn(info);

        TraineeInfoDTO actual = service.getTraineeByUsername(USERNAME);

        assertThat(actual).isEqualTo(info);
        verify(dao).findByUsername(USERNAME);
    }

    @Test
    void getTraineeByUsername_shouldThrowException_whenNotFound() {
        when(dao.findByUsername(NOT_FOUND_USERNAME)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.getTraineeByUsername(NOT_FOUND_USERNAME));

        assertThat(exception.getMessage()).isEqualTo(String.format(TRAINEE_NOT_FOUND_BY_USERNAME, NOT_FOUND_USERNAME));
        verify(dao).findByUsername(NOT_FOUND_USERNAME);
    }

    @Test
    void getTraineeByUsername_shouldThrowException_whenUsernameIsBlank() {
        doThrow(new ValidationFailedException(USERNAME_CANNOT_BE_NULL)).when(userInputValidator).validateUsername(BLANK_USERNAME);

        ValidationFailedException exception = assertThrows(ValidationFailedException.class, () -> service.getTraineeByUsername(BLANK_USERNAME));

        assertThat(exception.getMessage()).isEqualTo(USERNAME_CANNOT_BE_NULL);
        verify(dao, never()).findByUsername(any());
    }

    @Test
    void getAllTrainees_shouldReturnAllTrainees_whenExist() {
        when(dao.findAll()).thenReturn(List.of(savedTrainee));
        when(mapper.toInfoDto(savedTrainee)).thenReturn(info);

        List<TraineeInfoDTO> actual = service.getAllTrainees();

        assertThat(actual).hasSize(1);
    }

    @Test
    void getAllTrainees_shouldReturnEmptyList_whenNoTrainees() {
        when(dao.findAll()).thenReturn(List.of());

        List<TraineeInfoDTO> actual = service.getAllTrainees();

        assertThat(actual).isEmpty();
    }

    @Test
    void createTrainee_shouldLogInfo_whenCreatingTrainee() {
        when(mapper.toEntity(request)).thenReturn(trainee);
        when(userProfileService.generateUsername(FIRST_NAME, LAST_NAME)).thenReturn(USERNAME);
        when(userProfileService.generatePassword()).thenReturn(RAW_PASSWORD);
        when(userProfileService.encodePassword(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(dao.save(any(Trainee.class))).thenReturn(savedTrainee);
        when(mapper.toDto(savedTrainee)).thenReturn(response);

        service.createTrainee(request);

        assertThat(logAppender.list)
                .filteredOn(log -> log.getLevel() == Level.INFO)
                .extracting(ILoggingEvent::getFormattedMessage)
                .anyMatch(message -> message.contains(FIRST_NAME) && message.contains(LAST_NAME))
                .anyMatch(message -> message.contains(USERNAME));
    }

    @Test
    void updateTrainersList_shouldUpdateTrainers_whenAllExist() {
        Trainer trainer1 = TestDataProvider.buildTrainer(VALID_ID, TRAINER_USERNAME1);
        Trainer trainer2 = TestDataProvider.buildTrainer(VALID_ID1, TRAINER_USERNAME2);
        Trainee updatedTrainee = TestDataProvider.buildTraineeWithTrainers(Set.of(trainer1, trainer2));
        TrainerInfoDTO trainerInfo1 = TestDataProvider.buildTrainerInfoDTO(TRAINER_USERNAME1);
        TrainerInfoDTO trainerInfo2 = TestDataProvider.buildTrainerInfoDTO(TRAINER_USERNAME2);

        when(trainerDAO.findByUsername(TRAINER_USERNAME1)).thenReturn(Optional.of(trainer1));
        when(trainerDAO.findByUsername(TRAINER_USERNAME2)).thenReturn(Optional.of(trainer2));
        when(dao.findByUsername(USERNAME)).thenReturn(Optional.of(updatedTrainee));
        when(trainerMapper.toInfoDto(trainer1)).thenReturn(trainerInfo1);
        when(trainerMapper.toInfoDto(trainer2)).thenReturn(trainerInfo2);

        List<TrainerInfoDTO> actual = service.updateTrainersList(trainerAssignmentUpdateDTO);

        verify(userInputValidator).validate(trainerAssignmentUpdateDTO, "Trainer assignment");
        verify(trainerDAO).findByUsername(TRAINER_USERNAME1);
        verify(trainerDAO).findByUsername(TRAINER_USERNAME2);
        verify(dao).updateTrainersList(eq(USERNAME), anyList());
        verify(dao).findByUsername(USERNAME);
        verify(trainerMapper).toInfoDto(trainer1);
        verify(trainerMapper).toInfoDto(trainer2);
        assertThat(actual).containsExactlyInAnyOrder(trainerInfo1, trainerInfo2);
    }

    @Test
    void updateTrainersList_shouldThrow_whenTrainerNotFound() {
        when(trainerDAO.findByUsername(TRAINER_USERNAME1)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.updateTrainersList(trainerAssignmentUpdateDTO));

        assertThat(exception.getMessage()).contains("Trainer not found by username: trainer1");
        verify(userInputValidator).validate(trainerAssignmentUpdateDTO, "Trainer assignment");
        verify(trainerDAO).findByUsername(TRAINER_USERNAME1);
        verify(dao, never()).updateTrainersList(anyString(), anyList());
    }
}
