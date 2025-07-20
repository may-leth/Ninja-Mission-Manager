package com.may.Ninja.Mission.Manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.may.Ninja.Mission.Manager.dtos.MissionRequest;
import com.may.Ninja.Mission.Manager.models.Rank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class MissionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private MissionRequest validRequest;

    @BeforeEach
    void setUp(){
        validRequest = new MissionRequest(
                "Explorar el valle del fin",
                Rank.A,
                "Neji Hyuga",
                false
        );
    }

    private String asJsonString(Object object){
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception exception) {
            throw new RuntimeException("Failed to convert object to JSON string for testing", exception);
        }
    }

    @Test
    @DisplayName("GET /missions should return all missions")
    void getAllMissions_returnsListOfMissions() throws Exception {
        mockMvc.perform(get("/missions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(10)))
                .andExpect(jsonPath("$[0].name", is("Proteger al constructor de puentes Tazuna")));
    }

    @Test
    @DisplayName("GET /missions/{id} should return mission by id")
    void getMissionById_returnsMissionDetails() throws Exception{
        mockMvc.perform(get("/missions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Proteger al constructor de puentes Tazuna")))
                .andExpect(jsonPath("$.rank", is("C")))
                .andExpect(jsonPath("$.completed", is(false)));
    }


    @Test
    @DisplayName("GET /missions/{id} should return 404 when not found")
    void getMissionById_returnsNotFound_WhenIdDoesNotExist() throws Exception {
        mockMvc.perform(get("/missions/99"))
                .andExpect(status().isNotFound());
    }

    @Nested
    @DisplayName("POST /missions")
    class AddMissionsTests{

        @Test
        @DisplayName("should create a mission correctly and return a 201")
        void addMission_returnsCreatedMission() throws Exception{
            mockMvc.perform(post("/missions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(validRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name", is(validRequest.name())))
                    .andExpect(jsonPath("$.rank", is(validRequest.rank().toString())))
                    .andExpect(jsonPath("$.assignedTo", is(validRequest.assignedTo())))
                    .andExpect(jsonPath("$.completed", is(validRequest.completed())));
        }

        @Test
        @DisplayName("should return a 400 if the name is missing")
        void addMission_missingName_returnsBadRequest() throws Exception{
            MissionRequest request = new MissionRequest("", Rank.B, "Shikamaru", false);
            mockMvc.perform(post("/missions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return a 400 if the rank is missing")
        void addMission_missingRank_returnsBadRequest() throws Exception {
            MissionRequest request = new MissionRequest("Recoger flores", null, "Ino", false);
            mockMvc.perform(post("/missions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return a 400 if the assignedTo field is missing")
        void addMission_missingAssignedTo_returnsBadRequest() throws Exception {
            MissionRequest request = new MissionRequest("Entrenar en el bosque", Rank.C, null, false);
            mockMvc.perform(post("/missions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

}
