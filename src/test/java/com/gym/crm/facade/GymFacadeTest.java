package com.gym.crm.facade;

import com.gia.openapi.model.LoginChangeRequest;
import com.gia.openapi.model.LoginRequest;
import com.gym.crm.dto.common.AuthRequestDTO;
import com.gym.crm.dto.common.AuthResponseDTO;
import com.gym.crm.dto.common.PasswordChangeRequest;
import com.gym.crm.dto.common.ToggleActiveRequestDTO;
import com.gym.crm.dto.trainee.TraineeInfoDTO;
import com.gym.crm.dto.trainee.TraineeRequestDTO;
import com.gym.crm.dto.trainee.TraineeResponseDTO;
import com.gym.crm.dto.trainee.TraineeUpdateDTO;
import com.gym.crm.dto.trainee.TrainerAssignmentUpdateDTO;
import com.gym.crm.dto.trainer.TrainerInfoDTO;
import com.gym.crm.dto.trainer.TrainerRequestDTO;
import com.gym.crm.dto.trainer.TrainerResponseDTO;
import com.gym.crm.dto.trainer.TrainerUpdateDTO;
import com.gym.crm.dto.training.TrainingRequestDTO;
import com.gym.crm.dto.training.TrainingResponseDTO;
import com.gym.crm.search.filter.TraineeTrainingFilter;
import com.gym.crm.search.filter.TrainerTrainingFilter;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.TrainingService;
import com.gym.crm.service.UserService;
import com.gym.crm.service.common.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GymFacadeTest {
    private static final String FIRST_NAME = "Simone";
    private static final String LAST_NAME = "Radcliffe";
    private static final String USERNAME = "Simone.Radcliffe";
    private static final String PASSWORD = "password";
    private static final String NEW_PASSWORD = "newPassword";
    private static final String TRAINING_NAME = "Morning Cardio";
    private static final String TRAINING_TYPE_NAME = "Cardio";
    private static final long VALID_ID = 1L;

    private static final String AUTH_SUCCESS_MESSAGE = "Authentication successful!";

    @Mock
    private TraineeService traineeService;
    @Mock
    private TrainerService trainerService;
    @Mock
    private TrainingService trainingService;
    @Mock
    private UserService userService;
    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private GymFacade facade;

    private TraineeRequestDTO traineeRequestDTO;
    private TraineeUpdateDTO traineeUpdateDTO;
    private TraineeResponseDTO traineeResponseDTO;
    private TraineeInfoDTO traineeInfoDTO;
    private TrainerRequestDTO trainerRequestDTO;
    private TrainerUpdateDTO trainerUpdateDTO;
    private TrainerResponseDTO trainerResponseDTO;
    private TrainerInfoDTO trainerInfoDTO;
    private TrainingRequestDTO trainingRequestDTO;
    private TrainingResponseDTO trainingResponseDTO;
    private ToggleActiveRequestDTO toggleActiveRequestDTO;
    private TrainerAssignmentUpdateDTO trainerAssignmentUpdateDTO;
    private PasswordChangeRequest passwordChangeRequest;
    private TraineeTrainingFilter traineeTrainingFilter;
    private TrainerTrainingFilter trainerTrainingFilter;

    private LoginRequest loginRequest;
    private LoginChangeRequest loginChangeRequest;

    private AuthRequestDTO authRequestDTO;
    private AuthResponseDTO authResponseDTO;

    @BeforeEach
    void setUp() {
        traineeRequestDTO = buildTraineeRequestDTO();
        traineeUpdateDTO = buildTraineeUpdateDTO();
        traineeResponseDTO = buildTraineeResponseDTO();
        traineeInfoDTO = buildTraineeInfoDTO();

        trainerRequestDTO = buildTrainerRequestDTO();
        trainerUpdateDTO = buildTrainerUpdateDTO();
        trainerResponseDTO = buildTrainerResponseDTO();
        trainerInfoDTO = buildTrainerInfoDTO();

        trainingRequestDTO = buildTrainingRequestDTO();
        trainingResponseDTO = buildTrainingResponseDTO();

        toggleActiveRequestDTO = buildToggleActiveRequestDTO();
        trainerAssignmentUpdateDTO = buildTrainerAssignmentUpdateDTO();
        passwordChangeRequest = buildPasswordChangeRequest();
        traineeTrainingFilter = buildTraineeTrainingFilter();
        trainerTrainingFilter = buildTrainerTrainingFilter();

        authRequestDTO = buildAuthRequestDTO();
        authResponseDTO = buildAuthResponseDTO();

        loginRequest = buildLoginRequest();
        loginChangeRequest = buildLoginChangeRequest();
    }

    @Test
    void login_shouldSaveUserToContextAndReturnResponseDTO() {
        when(authenticationService.authenticate(authRequestDTO)).thenReturn(authResponseDTO);

        facade.login(loginRequest);

        ArgumentCaptor<AuthRequestDTO> dtoCaptor = ArgumentCaptor.forClass(AuthRequestDTO.class);
        verify(authenticationService).authenticate(dtoCaptor.capture());
        assertThat(dtoCaptor.getValue()).isEqualTo(authRequestDTO);
    }

    @Test
    void logout_shouldCallClearContext() {
        facade.logout(USERNAME);

        verify(authenticationService).logout();
    }

    @Test
    void createTrainee_shouldReturnResponseDTO() {
        when(traineeService.createTrainee(traineeRequestDTO)).thenReturn(traineeResponseDTO);

        TraineeResponseDTO actual = facade.createTrainee(traineeRequestDTO);

        assertThat(actual).isEqualTo(traineeResponseDTO);
        verify(traineeService).createTrainee(traineeRequestDTO);
    }

    @Test
    void updateTrainee_shouldReturnResponseDTO() {
        when(traineeService.updateTrainee(traineeUpdateDTO)).thenReturn(traineeResponseDTO);

        TraineeResponseDTO actual = facade.updateTrainee(traineeUpdateDTO, USERNAME);

        assertThat(actual).isEqualTo(traineeResponseDTO);
        verify(traineeService).updateTrainee(traineeUpdateDTO);
    }

    @Test
    void toggleActiveStatus_shouldCallUserService() {
        facade.toggleActiveStatus(toggleActiveRequestDTO, USERNAME);

        verify(userService).toggleActive(toggleActiveRequestDTO);
    }

    @Test
    void deleteTraineeByUsername_shouldDeleteTrainee() {
        facade.deleteTraineeByUsername(USERNAME, USERNAME);

        verify(traineeService).deleteByUsername(USERNAME);
    }

    @Test
    void getTraineeByUsername_shouldReturnInfoDTO() {
        when(traineeService.getTraineeByUsername(USERNAME)).thenReturn(traineeInfoDTO);

        TraineeInfoDTO actual = facade.getTraineeByUsername(USERNAME, USERNAME);

        assertThat(actual).isEqualTo(traineeInfoDTO);
        verify(traineeService).getTraineeByUsername(USERNAME);
    }

    @Test
    void getAllTrainees_shouldReturnListOfInfoDTOs() {
        when(traineeService.getAllTrainees()).thenReturn(List.of(traineeInfoDTO));

        List<TraineeInfoDTO> actual = facade.getAllTrainees(USERNAME);

        assertThat(actual)
                .hasSize(1)
                .contains(traineeInfoDTO);
        verify(traineeService).getAllTrainees();
    }

    @Test
    void getAllTrainees_shouldReturnEmptyList_whenNoTrainees() {
        when(traineeService.getAllTrainees()).thenReturn(List.of());

        List<TraineeInfoDTO> actual = facade.getAllTrainees(USERNAME);

        assertThat(actual).isEmpty();
        verify(traineeService).getAllTrainees();
    }

    @Test
    void updateTraineeTrainersList_shouldCallService() {
        facade.updateTraineeTrainersList(trainerAssignmentUpdateDTO, USERNAME);

        verify(traineeService).updateTrainersList(trainerAssignmentUpdateDTO);
    }

    @Test
    void createTrainer_shouldReturnResponseDTO() {
        when(trainerService.createTrainer(trainerRequestDTO)).thenReturn(trainerResponseDTO);

        TrainerResponseDTO actual = facade.createTrainer(trainerRequestDTO);

        assertThat(actual).isEqualTo(trainerResponseDTO);
        verify(trainerService).createTrainer(trainerRequestDTO);
    }

    @Test
    void updateTrainer_shouldReturnResponseDTO() {
        when(trainerService.updateTrainer(trainerUpdateDTO)).thenReturn(trainerResponseDTO);

        TrainerResponseDTO actual = facade.updateTrainer(trainerUpdateDTO, USERNAME);

        assertThat(actual).isEqualTo(trainerResponseDTO);
        verify(trainerService).updateTrainer(trainerUpdateDTO);
    }

    @Test
    void getTrainerByUsername_shouldReturnInfoDTO() {
        when(trainerService.getTrainerByUsername(USERNAME)).thenReturn(trainerInfoDTO);

        TrainerInfoDTO actual = facade.getTrainerByUsername(USERNAME, USERNAME);

        assertThat(actual).isEqualTo(trainerInfoDTO);
        verify(trainerService).getTrainerByUsername(USERNAME);
    }

    @Test
    void getAllTrainers_shouldReturnListOfInfoDTOs() {
        when(trainerService.getAllTrainers()).thenReturn(List.of(trainerInfoDTO));

        List<TrainerInfoDTO> actual = facade.getAllTrainers(USERNAME);

        assertThat(actual)
                .hasSize(1)
                .contains(trainerInfoDTO);
        verify(trainerService).getAllTrainers();
    }

    @Test
    void getAllTrainers_shouldReturnEmptyList_whenNoTrainers() {
        when(trainerService.getAllTrainers()).thenReturn(List.of());

        List<TrainerInfoDTO> actual = facade.getAllTrainers(USERNAME);

        assertThat(actual).isEmpty();
        verify(trainerService).getAllTrainers();
    }

    @Test
    void getTrainersNotAssignedToTrainee_shouldReturnListOfInfoDTOs() {
        when(trainerService.getNotAssignedToTrainee(USERNAME)).thenReturn(List.of(trainerInfoDTO));

        List<TrainerInfoDTO> actual = facade.getTrainersNotAssignedToTrainee(USERNAME, USERNAME);

        assertThat(actual)
                .hasSize(1)
                .contains(trainerInfoDTO);
        verify(trainerService).getNotAssignedToTrainee(USERNAME);
    }

    @Test
    void changePassword_shouldCallUserService() {
        facade.changePassword(loginChangeRequest, USERNAME);

        ArgumentCaptor<PasswordChangeRequest> dtoCaptor = ArgumentCaptor.forClass(PasswordChangeRequest.class);
        verify(userService).changePassword(dtoCaptor.capture());
        assertThat(dtoCaptor.getValue()).isEqualTo(passwordChangeRequest);
    }

    @Test
    void createTraining_shouldReturnResponseDTO() {
        when(trainingService.createTraining(trainingRequestDTO)).thenReturn(trainingResponseDTO);

        TrainingResponseDTO actual = facade.createTraining(trainingRequestDTO, USERNAME);

        assertThat(actual).isEqualTo(trainingResponseDTO);
        verify(trainingService).createTraining(trainingRequestDTO);
    }

    @Test
    void getTrainingById_shouldReturnResponseDTO() {
        when(trainingService.getTrainingById(VALID_ID)).thenReturn(trainingResponseDTO);

        TrainingResponseDTO actual = facade.getTrainingById(VALID_ID, USERNAME);

        assertThat(actual).isEqualTo(trainingResponseDTO);
        verify(trainingService).getTrainingById(VALID_ID);
    }

    @Test
    void getAllTrainings_shouldReturnListOfResponseDTOs() {
        when(trainingService.getAllTrainings()).thenReturn(List.of(trainingResponseDTO));

        List<TrainingResponseDTO> actual = facade.getAllTrainings(USERNAME);

        assertThat(actual)
                .hasSize(1)
                .contains(trainingResponseDTO);
        verify(trainingService).getAllTrainings();
    }

    @Test
    void getAllTrainings_shouldReturnEmptyList_whenNoTrainings() {
        when(trainingService.getAllTrainings()).thenReturn(List.of());

        List<TrainingResponseDTO> actual = facade.getAllTrainings(USERNAME);

        assertThat(actual).isEmpty();
        verify(trainingService).getAllTrainings();
    }

    @Test
    void getTraineeTrainingsByFilter_shouldReturnListOfResponseDTOs() {
        when(trainingService.getTraineeTrainings(traineeTrainingFilter)).thenReturn(List.of(trainingResponseDTO));

        List<TrainingResponseDTO> actual = facade.getTraineeTrainingsByFilter(traineeTrainingFilter, USERNAME);

        assertThat(actual)
                .hasSize(1)
                .contains(trainingResponseDTO);
        verify(trainingService).getTraineeTrainings(traineeTrainingFilter);
    }

    @Test
    void getTrainerTrainingsByFilter_shouldReturnListOfResponseDTOs() {
        when(trainingService.getTrainerTrainings(trainerTrainingFilter)).thenReturn(List.of(trainingResponseDTO));

        List<TrainingResponseDTO> actual = facade.getTrainerTrainingsByFilter(trainerTrainingFilter, USERNAME);

        assertThat(actual)
                .hasSize(1)
                .contains(trainingResponseDTO);
        verify(trainingService).getTrainerTrainings(trainerTrainingFilter);
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

    private TraineeResponseDTO buildTraineeResponseDTO() {
        return TraineeResponseDTO.builder()
                .id(VALID_ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .username(USERNAME)
                .isActive(true)
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

    private TrainerResponseDTO buildTrainerResponseDTO() {
        return TrainerResponseDTO.builder()
                .id(2L)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .username(USERNAME)
                .isActive(true)
                .build();
    }

    private TrainerInfoDTO buildTrainerInfoDTO() {
        return TrainerInfoDTO.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .username(USERNAME)
                .isActive(true)
                .specialization(TRAINING_TYPE_NAME)
                .build();
    }

    private TrainingRequestDTO buildTrainingRequestDTO() {
        return TrainingRequestDTO.builder()
                .traineeUsername(USERNAME)
                .trainerUsername(USERNAME)
                .trainingName(TRAINING_NAME)
                .trainingTypeName(TRAINING_TYPE_NAME)
                .trainingDate(LocalDate.of(2024, 1, 15))
                .trainingDuration(60)
                .build();
    }

    private TrainingResponseDTO buildTrainingResponseDTO() {
        return TrainingResponseDTO.builder()
                .id(VALID_ID)
                .traineeUsername(USERNAME)
                .trainerUsername(USERNAME)
                .trainingName(TRAINING_NAME)
                .trainingTypeName(TRAINING_TYPE_NAME)
                .trainingDate(LocalDate.of(2024, 1, 15))
                .trainingDuration(60)
                .build();
    }

    private ToggleActiveRequestDTO buildToggleActiveRequestDTO() {
        return ToggleActiveRequestDTO.builder()
                .username(USERNAME)
                .isActive(true)
                .build();
    }

    private TrainerAssignmentUpdateDTO buildTrainerAssignmentUpdateDTO() {
        return TrainerAssignmentUpdateDTO.builder()
                .traineeUsername(USERNAME)
                .trainerUsernames(List.of(USERNAME))
                .build();
    }

    private PasswordChangeRequest buildPasswordChangeRequest() {
        return PasswordChangeRequest.builder()
                .username(USERNAME)
                .oldPassword(PASSWORD)
                .newPassword("newPassword")
                .build();
    }

    private TraineeTrainingFilter buildTraineeTrainingFilter() {
        return TraineeTrainingFilter.builder()
                .username(USERNAME)
                .build();
    }

    private TrainerTrainingFilter buildTrainerTrainingFilter() {
        return TrainerTrainingFilter.builder()
                .username(USERNAME)
                .build();
    }

    private AuthRequestDTO buildAuthRequestDTO() {
        return AuthRequestDTO.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .build();
    }

    private AuthResponseDTO buildAuthResponseDTO() {
        return AuthResponseDTO.builder()
                .username(USERNAME)
                .message(AUTH_SUCCESS_MESSAGE)
                .build();
    }

    private LoginRequest buildLoginRequest() {
        return new LoginRequest(USERNAME, PASSWORD);
    }

    private LoginChangeRequest buildLoginChangeRequest() {
        return new LoginChangeRequest(USERNAME, PASSWORD, NEW_PASSWORD);
    }
}
