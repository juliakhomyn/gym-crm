package com.gym.crm.service;

import com.gym.crm.dao.TrainerDAO;
import com.gym.crm.dto.trainer.TrainerInfoDTO;
import com.gym.crm.dto.trainer.TrainerRequestDTO;
import com.gym.crm.dto.trainer.TrainerResponseDTO;
import com.gym.crm.dto.trainer.TrainerUpdateDTO;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.exception.ValidationFailedException;
import com.gym.crm.mapper.TrainerMapper;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.TrainingType;
import com.gym.crm.model.User;
import com.gym.crm.service.common.UserInputValidator;
import com.gym.crm.service.common.UserProfileService;
import com.gym.crm.service.impl.TrainerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {
    private static final String FIRST_NAME = "Owen";
    private static final String LAST_NAME = "Castleberry";
    private static final String USERNAME = "Owen.Castleberry";
    private static final String NOT_FOUND_USERNAME = "Not.Found";
    private static final String BLANK_USERNAME = " ";
    private static final String ENCODED_PASSWORD = "encodedPassword";
    private static final String RAW_PASSWORD = "rawPassword";
    private static final String SPECIALIZATION = "Yoga";
    private static final long VALID_ID = 1L;
    private static final long INVALID_ID = -1L;
    private static final long NOT_FOUND_ID = 999L;

    private static final String TRAINER_CANNOT_BE_NULL = "Trainer cannot be null";
    private static final String TRAINER_NOT_FOUND_BY_ID = "Trainer not found by id: %s";
    private static final String TRAINER_NOT_FOUND_BY_USERNAME = "Trainer not found by username: %s";
    private static final String ID_CANNOT_BE_NULL = "ID cannot be null";
    private static final String ID_CANNOT_BE_NEGATIVE = "ID must be a positive number";
    private static final String USERNAME_CANNOT_BE_NULL = "Username cannot be null or empty";

    @Mock
    private TrainerDAO dao;
    @Mock
    private UserProfileService userProfileService;
    @Mock
    private TrainerMapper mapper;
    @Mock
    private UserInputValidator userInputValidator;

    @InjectMocks
    private TrainerServiceImpl service;

    private Trainer trainer;
    private Trainer savedTrainer;
    private TrainerRequestDTO request;
    private TrainerUpdateDTO updateDTO;
    private TrainerResponseDTO response;
    private TrainerInfoDTO info;

    @BeforeEach
    void setUp() {
        trainer = buildTrainer();
        request = buildTrainerRequestDTO();
        updateDTO = buildTrainerUpdateDTO();
        response = buildTrainerResponseDTO();
        info = buildTrainerInfoDTO();
        savedTrainer = trainer.toBuilder()
                .id(VALID_ID)
                .user(buildSavedUser())
                .build();
    }

    @Test
    void createTrainer_shouldSaveTrainerWithCredentials() {
        when(mapper.toEntity(request)).thenReturn(trainer);
        when(userProfileService.generateUsername(FIRST_NAME, LAST_NAME)).thenReturn(USERNAME);
        when(userProfileService.generatePassword()).thenReturn(RAW_PASSWORD);
        when(userProfileService.encodePassword(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(dao.save(any(Trainer.class))).thenReturn(savedTrainer);
        when(mapper.toDto(savedTrainer)).thenReturn(response);

        TrainerResponseDTO actual = service.createTrainer(request);

        assertThat(actual).isEqualTo(response);
        verify(userInputValidator).validate(request, "Trainer");
        verify(mapper).toEntity(request);
        verify(userProfileService).generateUsername(FIRST_NAME, LAST_NAME);
        verify(userProfileService).generatePassword();
        verify(userProfileService).encodePassword(RAW_PASSWORD);
        verify(dao).save(any(Trainer.class));
        verify(mapper).toDto(savedTrainer);
    }

    @Test
    void createTrainer_shouldThrowException_whenTrainerIsNull() {
        doThrow(new ValidationFailedException(TRAINER_CANNOT_BE_NULL)).when(userInputValidator).validate(null, "Trainer");

        ValidationFailedException exception = assertThrows(ValidationFailedException.class, () -> service.createTrainer(null));

        assertThat(exception.getMessage()).isEqualTo(TRAINER_CANNOT_BE_NULL);
    }

    @Test
    void updateTrainer_shouldUpdateTrainer_whenTrainerExists() {
        when(mapper.toEntity(updateDTO)).thenReturn(savedTrainer);
        when(dao.findById(VALID_ID)).thenReturn(Optional.ofNullable(savedTrainer));
        when(dao.update(any(Trainer.class))).thenReturn(savedTrainer);
        when(mapper.toDto(savedTrainer)).thenReturn(response);

        TrainerResponseDTO actual = service.updateTrainer(updateDTO);

        assertThat(actual).isEqualTo(response);
        verify(mapper).toEntity(updateDTO);
        verify(dao).update(any(Trainer.class));
        verify(mapper).toDto(savedTrainer);
    }

    @Test
    void updateTrainer_shouldThrowException_whenTrainerIsNull() {
        doThrow(new ValidationFailedException(TRAINER_CANNOT_BE_NULL)).when(userInputValidator).validate(null, "Trainer");

        ValidationFailedException exception = assertThrows(ValidationFailedException.class, () -> service.updateTrainer(null));

        assertThat(exception.getMessage()).isEqualTo(TRAINER_CANNOT_BE_NULL);
    }

    @Test
    void updateTrainer_shouldThrowException_whenTrainerNotFound() {
        TrainerUpdateDTO nonExistent = buildNonExistentTrainerUpdateDTO();
        when(mapper.toEntity(nonExistent)).thenReturn(buildNonExistentTrainer());
        when(dao.findById(NOT_FOUND_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.updateTrainer(nonExistent));

        assertThat(exception.getMessage()).isEqualTo(String.format(TRAINER_NOT_FOUND_BY_ID, NOT_FOUND_ID));
        verify(dao, never()).update(any(Trainer.class));
    }

    @Test
    void getTrainerById_shouldReturnTrainer_whenTrainerExists() {
        when(dao.findById(VALID_ID)).thenReturn(Optional.of(savedTrainer));
        when(mapper.toInfoDto(savedTrainer)).thenReturn(info);

        TrainerInfoDTO actual = service.getTrainerById(VALID_ID);

        assertThat(actual).isEqualTo(info);
    }

    @Test
    void getTrainerById_shouldThrowException_whenTrainerNotFound() {
        when(dao.findById(NOT_FOUND_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.getTrainerById(NOT_FOUND_ID));

        assertThat(exception.getMessage()).isEqualTo(String.format(TRAINER_NOT_FOUND_BY_ID, NOT_FOUND_ID));
    }

    @Test
    void getTrainerById_shouldThrow_whenIdIsNull() {
        doThrow(new ValidationFailedException(ID_CANNOT_BE_NULL)).when(userInputValidator).validateId(null);

        ValidationFailedException exception = assertThrows(ValidationFailedException.class, () -> service.getTrainerById(null));

        assertThat(exception.getMessage()).isEqualTo(ID_CANNOT_BE_NULL);
    }

    @Test
    void getTrainerById_shouldThrow_whenIdIsNegative() {
        doThrow(new ValidationFailedException(ID_CANNOT_BE_NEGATIVE)).when(userInputValidator).validateId(INVALID_ID);

        ValidationFailedException exception = assertThrows(ValidationFailedException.class, () -> service.getTrainerById(INVALID_ID));

        assertThat(exception.getMessage()).isEqualTo(ID_CANNOT_BE_NEGATIVE);
    }

    @Test
    void getTrainerByUsername_shouldReturnTrainer_whenExists() {
        when(dao.findByUsername(USERNAME)).thenReturn(Optional.of(trainer));
        when(mapper.toInfoDto(trainer)).thenReturn(info);

        TrainerInfoDTO actual = service.getTrainerByUsername(USERNAME);

        assertThat(actual).isEqualTo(info);
        verify(dao).findByUsername(USERNAME);
    }

    @Test
    void getTrainerByUsername_shouldThrowException_whenNotFound() {
        when(dao.findByUsername(NOT_FOUND_USERNAME)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.getTrainerByUsername(NOT_FOUND_USERNAME));

        assertThat(exception.getMessage()).isEqualTo(String.format(TRAINER_NOT_FOUND_BY_USERNAME, NOT_FOUND_USERNAME));
        verify(dao).findByUsername(NOT_FOUND_USERNAME);
    }

    @Test
    void getTrainerByUsername_shouldThrowException_whenUsernameIsBlank() {
        doThrow(new ValidationFailedException(USERNAME_CANNOT_BE_NULL)).when(userInputValidator).validateUsername(BLANK_USERNAME);

        ValidationFailedException exception = assertThrows(ValidationFailedException.class, () -> service.getTrainerByUsername(BLANK_USERNAME));

        assertThat(exception.getMessage()).isEqualTo(USERNAME_CANNOT_BE_NULL);
        verify(dao, never()).findByUsername(any());
    }

    @Test
    void getAllTrainers_shouldReturnAllTrainers_whenExist() {
        when(dao.findAll()).thenReturn(List.of(savedTrainer));
        when(mapper.toInfoDto(savedTrainer)).thenReturn(info);

        List<TrainerInfoDTO> actual = service.getAllTrainers();

        assertThat(actual).hasSize(1);
    }

    @Test
    void getAllTrainers_shouldReturnEmptyList_whenNoTrainers() {
        when(dao.findAll()).thenReturn(List.of());

        List<TrainerInfoDTO> actual = service.getAllTrainers();

        assertThat(actual).isEmpty();
    }

    @Test
    void getNotAssignedToTrainee_shouldReturnListOfTrainerInfoDTOs() {
        Trainer trainer1 = buildTrainer(1L, "trainer1");
        Trainer trainer2 = buildTrainer(2L, "trainer2");
        TrainerInfoDTO trainerInfoDTO1 = buildNotAssignedTrainerInfoDTO("trainer1");
        TrainerInfoDTO trainerInfoDTO2 = buildNotAssignedTrainerInfoDTO("trainer2");

        when(dao.findNotAssignedToTrainee(USERNAME)).thenReturn(List.of(trainer1, trainer2));
        when(mapper.toInfoDto(trainer1)).thenReturn(trainerInfoDTO1);
        when(mapper.toInfoDto(trainer2)).thenReturn(trainerInfoDTO2);

        List<TrainerInfoDTO> actual = service.getNotAssignedToTrainee(USERNAME);

        assertThat(actual)
                .hasSize(2)
                .containsExactlyInAnyOrder(trainerInfoDTO1, trainerInfoDTO2);
        verify(userInputValidator).validateUsername(USERNAME);
        verify(dao).findNotAssignedToTrainee(USERNAME);
        verify(mapper).toInfoDto(trainer1);
        verify(mapper).toInfoDto(trainer2);
    }

    @Test
    void getNotAssignedToTrainee_shouldReturnEmptyList_whenNoTrainers() {
        when(dao.findNotAssignedToTrainee(USERNAME)).thenReturn(List.of());

        List<TrainerInfoDTO> actual = service.getNotAssignedToTrainee(USERNAME);

        assertThat(actual).isEmpty();
        verify(userInputValidator).validateUsername(USERNAME);
        verify(dao).findNotAssignedToTrainee(USERNAME);
        verify(mapper, never()).toInfoDto(any());
    }

    @Test
    void getNotAssignedToTrainee_shouldThrow_whenUsernameIsInvalid() {
        doThrow(new ValidationFailedException(USERNAME_CANNOT_BE_NULL)).when(userInputValidator).validateUsername(BLANK_USERNAME);

        ValidationFailedException exception = assertThrows(ValidationFailedException.class, () -> service.getNotAssignedToTrainee(BLANK_USERNAME));

        assertThat(exception.getMessage()).isEqualTo(USERNAME_CANNOT_BE_NULL);
        verify(userInputValidator).validateUsername(BLANK_USERNAME);
        verify(dao, never()).findNotAssignedToTrainee(any());
        verify(mapper, never()).toInfoDto(any());
    }

    private Trainer buildTrainer() {
        return Trainer.builder()
                .user(buildUser())
                .specialization(buildTrainingType())
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

    private TrainingType buildTrainingType() {
        return TrainingType.builder().trainingTypeName(SPECIALIZATION).build();
    }

    private Trainer buildNonExistentTrainer() {
        User user = User.builder()
                .id(NOT_FOUND_ID)
                .build();

        return savedTrainer.toBuilder()
                .id(NOT_FOUND_ID)
                .user(user)
                .build();
    }

    private TrainerRequestDTO buildTrainerRequestDTO() {
        return TrainerRequestDTO.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .build();
    }

    private TrainerUpdateDTO buildTrainerUpdateDTO() {
        return TrainerUpdateDTO.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .build();
    }

    private TrainerUpdateDTO buildNonExistentTrainerUpdateDTO() {
        return TrainerUpdateDTO.builder()
                .id(NOT_FOUND_ID)
                .build();
    }

    private TrainerResponseDTO buildTrainerResponseDTO() {
        return TrainerResponseDTO.builder()
                .id(VALID_ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .username(USERNAME)
                .isActive(true)
                .specialization(SPECIALIZATION)
                .build();
    }

    private TrainerInfoDTO buildTrainerInfoDTO() {
        return TrainerInfoDTO.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .username(USERNAME)
                .isActive(true)
                .specialization(SPECIALIZATION)
                .build();
    }

    private Trainer buildTrainer(Long id, String username) {
        return Trainer.builder()
                .id(id)
                .user(User.builder().username(username).build())
                .build();
    }

    private TrainerInfoDTO buildNotAssignedTrainerInfoDTO(String username) {
        return TrainerInfoDTO.builder().username(username).build();
    }
}
