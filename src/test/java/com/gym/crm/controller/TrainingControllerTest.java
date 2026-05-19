package com.gym.crm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gia.openapi.model.TrainingCreateRequest;
import com.gia.openapi.model.TrainingTypeResponse;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TrainingControllerTest {
    private static final String TRAINEE_USERNAME = "Simone.Radcliffe";
    private static final String BASE_URL = "/api/v1";

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mockMvc;

    @Mock
    private GymFacade facade;

    @BeforeEach
    void setUp() {
        TrainingController controller = new TrainingController(facade);

        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(mapper))
                .addPlaceholderValue("app.api.base-path", "/api/v1")
                .build();
    }

    @Test
    void addTraining_shouldReturnOk_whenValid() throws Exception {
        TrainingCreateRequest request = TestDataProvider.buildTrainingCreateRequest();

        mockMvc.perform(post(BASE_URL + "/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        verify(facade).createTraining(any(TrainingCreateRequest.class));
    }

    @Test
    void addTraining_shouldReturnBadRequest_whenRequiredFieldsMissing() throws Exception {
        TrainingCreateRequest request = new TrainingCreateRequest();
        request.setTraineeUsername(TRAINEE_USERNAME);

        mockMvc.perform(post(BASE_URL + "/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(facade);
    }

    @Test
    void getTrainingTypes_shouldReturnTrainingTypes_whenExist() throws Exception {
        List<TrainingTypeResponse> response = List.of(TestDataProvider.buildTrainingTypeResponse());

        when(facade.getTrainingTypes()).thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/trainings/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(response.get(0).getId()))
                .andExpect(jsonPath("$[0].name").value(response.get(0).getName()));
        verify(facade).getTrainingTypes();
    }
}
