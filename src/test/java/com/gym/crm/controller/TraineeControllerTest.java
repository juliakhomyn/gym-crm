package com.gym.crm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gia.openapi.model.ActivationStatusRequest;
import com.gia.openapi.model.AssignedTrainerResponse;
import com.gia.openapi.model.TraineeAssignedTrainersUpdateRequest;
import com.gia.openapi.model.TraineeAssignedTrainersUpdateResponse;
import com.gia.openapi.model.TraineeCreateRequest;
import com.gia.openapi.model.TraineeCreateResponse;
import com.gia.openapi.model.TraineeGetResponse;
import com.gia.openapi.model.TraineeUpdateRequest;
import com.gia.openapi.model.TraineeUpdateResponse;
import com.gym.crm.facade.GymFacade;
import com.gym.crm.testutils.TestDataProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TraineeControllerTest {
    private static final String USERNAME = "Simone.Radcliffe";
    private static final String TRAINER_USERNAME = "Owen.Castleberry";
    private static final String BASE_URL = "/api/v1/trainees";

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mockMvc;

    @Mock
    private GymFacade facade;

    @BeforeEach
    void setUp() {
        TraineeController controller = new TraineeController(facade);

        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(mapper))
                .addPlaceholderValue("app.api.base-path", "/api/v1")
                .build();
    }

    @Test
    void register_shouldReturnCredentials_whenValid() throws Exception {
        TraineeCreateRequest request = TestDataProvider.buildTraineeCreateRequest();
        TraineeCreateResponse response = TestDataProvider.buildTraineeCreateResponse();

        when(facade.createTrainee(request)).thenReturn(response);

        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(response.getUsername()))
                .andExpect(jsonPath("$.password").value(response.getPassword()));
        verify(facade).createTrainee(any(TraineeCreateRequest.class));
    }

    @Test
    void register_shouldReturnBadRequest_whenFirstNameMissing() throws Exception {
        TraineeCreateRequest request = new TraineeCreateRequest();
        request.setLastName("Radcliffe");

        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(facade);
    }

    @Test
    void register_shouldReturnCredentials_whenOptionalFieldsOmitted() throws Exception {
        TraineeCreateRequest request = TestDataProvider.buildTraineeCreateRequestOnlyRequiredFields();
        TraineeCreateResponse response = TestDataProvider.buildTraineeCreateResponse();

        when(facade.createTrainee(any(TraineeCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(response.getUsername()))
                .andExpect(jsonPath("$.password").value(response.getPassword()));
    }

    @Test
    void getTraineeProfile_shouldReturnTrainee_whenExists() throws Exception {
        TraineeGetResponse response = TestDataProvider.buildTraineeGetResponse();

        when(facade.getTraineeByUsername(USERNAME)).thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/" + USERNAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(response.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(response.getLastName()))
                .andExpect(jsonPath("$.dateOfBirth").value(response.getDateOfBirth().toString()))
                .andExpect(jsonPath("$.address").value(response.getAddress()))
                .andExpect(jsonPath("$.isActive").value(response.getIsActive()))
                .andExpect(jsonPath("$.trainers").isArray())
                .andExpect(jsonPath("$.trainers.length()").value(response.getTrainers().size()))
                .andExpect(jsonPath("$.trainers[0].username").value(response.getTrainers().get(0).getUsername()));
        verify(facade).getTraineeByUsername(USERNAME);
    }

    @Test
    void updateTraineeProfile_shouldReturnResponse_whenValid() throws Exception {
        TraineeUpdateRequest request = TestDataProvider.buildTraineeUpdateRequest();
        TraineeUpdateResponse response = TestDataProvider.buildTraineeUpdateResponse();

        when(facade.updateTrainee(request, USERNAME)).thenReturn(response);

        mockMvc.perform(put(BASE_URL + "/" + USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(response.getUsername()))
                .andExpect(jsonPath("$.firstName").value(response.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(response.getLastName()))
                .andExpect(jsonPath("$.dateOfBirth").value(response.getDateOfBirth().toString()))
                .andExpect(jsonPath("$.address").value(response.getAddress()))
                .andExpect(jsonPath("$.isActive").value(response.getIsActive()))
                .andExpect(jsonPath("$.trainers").isArray())
                .andExpect(jsonPath("$.trainers.length()").value(response.getTrainers().size()))
                .andExpect(jsonPath("$.trainers[0].username").value(response.getTrainers().get(0).getUsername()));
        verify(facade).updateTrainee(any(TraineeUpdateRequest.class), any(String.class));
    }

    @Test
    void deleteTrainee_shouldDeleteTrainee_whenExists() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/" + USERNAME))
                .andExpect(status().isOk());

        verify(facade).deleteTraineeByUsername(USERNAME);
    }

    @Test
    void toggleActive_shouldChangeIsActive_whenValid() throws Exception {
        ActivationStatusRequest request = TestDataProvider.buildActivationStatusRequest();

        mockMvc.perform(patch(BASE_URL + "/" + USERNAME + "/activation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        verify(facade).toggleActiveStatus(request, USERNAME);
    }

    @Test
    void updateTraineeTrainers_shouldReturnList_whenValid() throws Exception {
        TraineeAssignedTrainersUpdateRequest request = new TraineeAssignedTrainersUpdateRequest(List.of(TRAINER_USERNAME));
        TraineeAssignedTrainersUpdateResponse response = TestDataProvider.buildTraineeAssignedTrainersUpdateResponse();

        when(facade.updateTraineeTrainersList(request, USERNAME)).thenReturn(response);

        mockMvc.perform(put(BASE_URL + "/" + USERNAME + "/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(response.getTrainers().size()))
                .andExpect(jsonPath("$.trainers[0].username").value(response.getTrainers().get(0).getUsername()))
                .andExpect(jsonPath("$.trainers[0].firstName").value(response.getTrainers().get(0).getFirstName()))
                .andExpect(jsonPath("$.trainers[0].lastName").value(response.getTrainers().get(0).getLastName()))
                .andExpect(jsonPath("$.trainers[0].specialization").value(response.getTrainers().get(0).getSpecialization()));
        verify(facade).updateTraineeTrainersList(request, USERNAME);
    }

    @Test
    void getAvailableTrainers_shouldReturnList_whenValid() throws Exception {
        List<AssignedTrainerResponse> response = List.of(TestDataProvider.buildAssignedTrainerResponse());

        when(facade.getTrainersNotAssignedToTrainee(USERNAME)).thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/" + USERNAME + "/available-trainers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(response.size()))
                .andExpect(jsonPath("$[0].username").value(response.get(0).getUsername()))
                .andExpect(jsonPath("$[0].firstName").value(response.get(0).getFirstName()))
                .andExpect(jsonPath("$[0].lastName").value(response.get(0).getLastName()))
                .andExpect(jsonPath("$[0].specialization").value(response.get(0).getSpecialization()));
        verify(facade).getTrainersNotAssignedToTrainee(USERNAME);
    }
}
