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
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.exception.ValidationFailedException;
import com.gym.crm.mapper.TraineeMapper;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.User;
import com.gym.crm.service.common.UserInputValidator;
import com.gym.crm.service.common.UserProfileService;
import com.gym.crm.service.impl.TraineeServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
public class TraineeServiceImplTest {
    private static final String FIRST_NAME = "Owen";
    private static final String LAST_NAME = "Castleberry";
    private static final String USERNAME = "Owen.Castleberry";
    private static final String NOT_FOUND_USERNAME = "Not.Found";
    private static final String BLANK_USERNAME = " ";
    private static final String ENCODED_PASSWORD = "encodedPassword";
    private static final String RAW_PASSWORD = "rawPassword";
    private static final long VALID_ID = 1L;
    private static final long INVALID_ID = -1L;
    private static final long NOT_FOUND_ID = 999L;

    private static final String TRAINEE_CANNOT_BE_NULL = "Trainee cannot be null";
    private static final String TRAINEE_NOT_FOUND_BY_ID = "Trainee not found by id: %s";
    private static final String TRAINEE_NOT_FOUND_BY_USERNAME = "Trainee not found by username: %s";
    private static final String ID_CANNOT_BE_NULL = "ID cannot be null";
    private static final String ID_CANNOT_BE_NEGATIVE = "ID must be a positive number";
    private static final String USERNAME_CANNOT_BE_NULL = "Username cannot be null or empty";

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

    @InjectMocks
    private TraineeServiceImpl service;

    private Trainee trainee;
    private Trainee savedTrainee;
    private TraineeRequestDTO request;
    private TraineeUpdateDTO updateDTO;
    private TraineeResponseDTO response;
    private TraineeInfoDTO info;
    private ListAppender<ILoggingEvent> logAppender;

    @BeforeEach
    void setUp() {
        trainee = buildTrainee();
        request = buildTraineeRequestDTO();
        updateDTO = buildTraineeUpdateDTO();
        response = buildTraineeResponseDTO();
        info = buildTraineeInfoDTO();
        savedTrainee = trainee.toBuilder()
                .id(VALID_ID)
                .user(buildSavedUser())
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
        when(mapper.toEntity(request)).thenReturn(trainee);
        when(userProfileService.generateUsername(FIRST_NAME, LAST_NAME)).thenReturn(USERNAME);
        when(userProfileService.generatePassword()).thenReturn(RAW_PASSWORD);
        when(userProfileService.encodePassword(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(dao.save(any(Trainee.class))).thenReturn(savedTrainee);
        when(mapper.toDto(savedTrainee)).thenReturn(response);

        TraineeResponseDTO actual = service.createTrainee(request);

        assertThat(actual).isEqualTo(response);
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
    void updateTrainee_shouldUpdateTrainee_whenTraineeExists() {
        when(mapper.toEntity(updateDTO)).thenReturn(savedTrainee);
        when(dao.findById(VALID_ID)).thenReturn(Optional.ofNullable(savedTrainee));
        when(dao.update(any(Trainee.class))).thenReturn(savedTrainee);
        when(mapper.toDto(savedTrainee)).thenReturn(response);

        TraineeResponseDTO actual = service.updateTrainee(updateDTO);

        assertThat(actual).isEqualTo(response);
        verify(mapper).toEntity(updateDTO);
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
        TraineeUpdateDTO nonExistent = buildNonExistentTraineeUpdateDTO();
        when(mapper.toEntity(nonExistent)).thenReturn(buildNonExistentTrainee());
        when(dao.findById(NOT_FOUND_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.updateTrainee(nonExistent));

        assertThat(exception.getMessage()).isEqualTo(String.format(TRAINEE_NOT_FOUND_BY_ID, NOT_FOUND_ID));
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
        Trainer trainer1 = buildTrainer("trainer1");
        Trainer trainer2 = buildTrainer("trainer2");
        TrainerAssignmentUpdateDTO dto = buildValidDto();

        when(trainerDAO.findByUsername("trainer1")).thenReturn(Optional.of(trainer1));
        when(trainerDAO.findByUsername("trainer2")).thenReturn(Optional.of(trainer2));

        service.updateTrainersList(dto);

        verify(userInputValidator).validate(dto, "Trainer assignment");
        verify(trainerDAO).findByUsername("trainer1");
        verify(trainerDAO).findByUsername("trainer2");

        ArgumentCaptor<List<Trainer>> captor = ArgumentCaptor.forClass(List.class);
        verify(dao).updateTrainersList(eq(USERNAME), captor.capture());
        List<Trainer> trainersPassed = captor.getValue();
        assertThat(trainersPassed).containsExactly(trainer1, trainer2);
    }

    @Test
    void updateTrainersList_shouldThrow_whenTrainerNotFound() {
        TrainerAssignmentUpdateDTO dto = buildValidDto();

        when(trainerDAO.findByUsername("trainer1")).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.updateTrainersList(dto));

        assertThat(exception.getMessage()).contains("Trainer not found by username: trainer1");
        verify(userInputValidator).validate(dto, "Trainer assignment");
        verify(trainerDAO).findByUsername("trainer1");
        verify(dao, never()).updateTrainersList(anyString(), anyList());
    }

    @Test
    void updateTrainersList_shouldCallValidationService() {
        TrainerAssignmentUpdateDTO dto = buildValidDto();
        Trainer trainer1 = buildTrainer("trainer1");
        Trainer trainer2 = buildTrainer("trainer2");

        when(trainerDAO.findByUsername("trainer1")).thenReturn(Optional.of(trainer1));
        when(trainerDAO.findByUsername("trainer2")).thenReturn(Optional.of(trainer2));

        service.updateTrainersList(dto);

        verify(userInputValidator).validate(dto, "Trainer assignment");
    }

    private Trainee buildTrainee() {
        return Trainee.builder()
                .user(buildUser())
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("123 Main St")
                .build();
    }

    private TraineeRequestDTO buildTraineeRequestDTO() {
        return TraineeRequestDTO.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .build();
    }

    private TraineeUpdateDTO buildTraineeUpdateDTO() {
        return TraineeUpdateDTO.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .build();
    }

    private TraineeUpdateDTO buildNonExistentTraineeUpdateDTO() {
        return TraineeUpdateDTO.builder()
                .id(NOT_FOUND_ID)
                .build();
    }

    private TraineeResponseDTO buildTraineeResponseDTO() {
        return TraineeResponseDTO.builder()
                .id(VALID_ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .username(USERNAME)
                .isActive(true)
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("123 Main St")
                .build();
    }

    private TraineeInfoDTO buildTraineeInfoDTO() {
        return TraineeInfoDTO.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .username(USERNAME)
                .isActive(true)
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("123 Main St")
                .build();
    }

    private User buildUser() {
        return User.builder()
                .id(VALID_ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .username(USERNAME)
                .password(ENCODED_PASSWORD)
                .isActive(true)
                .build();
    }

    private User buildSavedUser() {
        return User.builder()
                .id(VALID_ID)
                .username(USERNAME)
                .password(ENCODED_PASSWORD)
                .isActive(true)
                .build();
    }

    private Trainee buildNonExistentTrainee() {
        User user = User.builder().id(NOT_FOUND_ID).build();

        return savedTrainee.toBuilder()
                .id(NOT_FOUND_ID)
                .user(user)
                .build();
    }

    private TrainerAssignmentUpdateDTO buildValidDto() {
        return TrainerAssignmentUpdateDTO.builder()
                .traineeUsername(USERNAME)
                .trainerUsernames(List.of("trainer1", "trainer2"))
                .build();
    }

    private Trainer buildTrainer(String username) {
        return Trainer.builder()
                .id(VALID_ID)
                .user(User.builder().username(username).build())
                .build();
    }
}
