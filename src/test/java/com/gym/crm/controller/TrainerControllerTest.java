package com.gym.crm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gia.openapi.model.ActivationStatusRequest;
import com.gia.openapi.model.TrainerCreateRequest;
import com.gia.openapi.model.TrainerCreateResponse;
import com.gia.openapi.model.TrainerGetResponse;
import com.gia.openapi.model.TrainerUpdateRequest;
import com.gia.openapi.model.TrainerUpdateResponse;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TrainerControllerTest {
    private static final String USERNAME = "Owen.Castleberry";
    private static final String BASE_URL = "/api/v1/trainers";

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mockMvc;

    @Mock
    private GymFacade facade;

    @BeforeEach
    void setUp() {
        TrainerController controller = new TrainerController(facade);

        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(mapper))
                .addPlaceholderValue("app.api.base-path", "/api/v1")
                .build();
    }

    @Test
    void register_shouldReturnCredentials_whenValid() throws Exception {
        TrainerCreateRequest request = TestDataProvider.buildTrainerCreateRequest();
        TrainerCreateResponse response = TestDataProvider.buildTrainerCreateResponse();

        when(facade.createTrainer(request)).thenReturn(response);

        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(response.getUsername()))
                .andExpect(jsonPath("$.password").value(response.getPassword()));
        verify(facade).createTrainer(any(TrainerCreateRequest.class));
    }

    @Test
    void register_shouldReturnBadRequest_whenFirstNameMissing() throws Exception {
        TrainerCreateRequest request = new TrainerCreateRequest();
        request.setLastName("Castleberry");

        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(facade);
    }

    @Test
    void getTrainerProfile_shouldReturnTrainer_whenExists() throws Exception {
        TrainerGetResponse response = TestDataProvider.buildTrainerGetResponse();

        when(facade.getTrainerByUsername(USERNAME)).thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/" + USERNAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(response.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(response.getLastName()))
                .andExpect(jsonPath("$.specialization").value(response.getSpecialization()))
                .andExpect(jsonPath("$.isActive").value(response.getIsActive()))
                .andExpect(jsonPath("$.trainees").isArray())
                .andExpect(jsonPath("$.trainees.length()").value(response.getTrainees().size()))
                .andExpect(jsonPath("$.trainees[0].username").value(response.getTrainees().get(0).getUsername()));
        verify(facade).getTrainerByUsername(USERNAME);
    }

    @Test
    void updateTrainerProfile_shouldReturnResponse_whenValid() throws Exception {
        TrainerUpdateRequest request = TestDataProvider.buildTrainerUpdateRequest();
        TrainerUpdateResponse response = TestDataProvider.buildTrainerUpdateResponse();

        when(facade.updateTrainer(request, USERNAME)).thenReturn(response);

        mockMvc.perform(put(BASE_URL + "/" + USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(response.getUsername()))
                .andExpect(jsonPath("$.firstName").value(response.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(response.getLastName()))
                .andExpect(jsonPath("$.specialization").value(response.getSpecialization()))
                .andExpect(jsonPath("$.isActive").value(response.getIsActive()))
                .andExpect(jsonPath("$.trainees").isArray())
                .andExpect(jsonPath("$.trainees.length()").value(response.getTrainees().size()))
                .andExpect(jsonPath("$.trainees[0].username").value(response.getTrainees().get(0).getUsername()));
        verify(facade).updateTrainer(any(TrainerUpdateRequest.class), any(String.class));
    }

    @Test
    void toggleActive_shouldReturnOk_whenValid() throws Exception {
        ActivationStatusRequest request = TestDataProvider.buildActivationStatusRequest();

        mockMvc.perform(patch(BASE_URL + "/" + USERNAME + "/activation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        verify(facade).toggleActiveStatus(request, USERNAME);
    }
}
