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
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    private ResultActions performPutRequest(String url, Object body) throws Exception {
        return mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(body)));
    }

    private ResultActions performPostRequest(String url, Object body) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(body)));
    }

    private ResultActions performDeleteRequest(String url) throws Exception{
        return mockMvc.perform(delete(url));
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
            performPostRequest("/missions", validRequest)
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
            performPostRequest("/missions", request)
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return a 400 if the rank is missing")
        void addMission_missingRank_returnsBadRequest() throws Exception {
            MissionRequest request = new MissionRequest("Recoger flores", null, "Ino", false);
            performPostRequest("/missions", request)
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return a 400 if the assignedTo field is missing")
        void addMission_missingAssignedTo_returnsBadRequest() throws Exception {
            MissionRequest request = new MissionRequest("Entrenar en el bosque", Rank.C, null, false);
            performPostRequest("/missions", request)
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /missions/{id} Update Existing Missions")
    class UpdateMissionsTest{

        private Long existingMissionId = 1L;

        @Test
        @DisplayName("Should update an existing mission correctly and return 200 OK")
        void updateMission_ReturnsOkAndUpdatedMission() throws Exception{
            MissionRequest updateRequest = new MissionRequest(
                    "Proteger al constructor de puentes de Tazuna V2",
                    Rank.S,
                    "Equipo 7: Naruto, Sasuke, Sakura, Kakashi",
                    true
            );

            performPutRequest("/missions/" + existingMissionId, updateRequest)
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is(updateRequest.name())))
                    .andExpect(jsonPath("$.rank", is(updateRequest.rank().toString())))
                    .andExpect(jsonPath("$.assignedTo", is(updateRequest.assignedTo())))
                    .andExpect(jsonPath("$.completed", is(updateRequest.completed())));

            mockMvc.perform(get("/missions/" + existingMissionId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name", is(updateRequest.name())))
                    .andExpect(jsonPath("$.rank", is(updateRequest.rank().toString())))
                    .andExpect(jsonPath("$.completed", is(updateRequest.completed())));
        }

        @Test
        @DisplayName("Should return 404 Not Found if mission to update does not exist")
        void updateMission_ReturnsNotFound_WhenIdDoesNotExist() throws Exception{
            Long nonExistenId = 999L;
            MissionRequest updateRequest = new MissionRequest(
                    "Misión inexistente", Rank.D, "nadie", false
            );

            performPutRequest("/missions/" + nonExistenId, updateRequest)
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 400 Bad Request if the name is missing in update request")
        void updateMission_ReturnsBadRequest_WhenNameIsMissing() throws Exception{
            MissionRequest invalidRequest = new MissionRequest(
                    "",Rank.C, "Naruto Uzumaki", false
            );

            performPutRequest("/missions/" + existingMissionId, invalidRequest)
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 Bad Request if the rank is missing in update request")
        void updateMission_ReturnsBadRequest_WhenRankIsMissing() throws Exception{
            MissionRequest invalidRequest = new MissionRequest(
                    "Misión importante", null, "Sasuke Uchiha", false
            );

            performPutRequest("/missions/" + existingMissionId, invalidRequest)
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 Bad Request if the assignedTo field is missing in update request")
        void updateMission_ReturnsBadRequest_WhenAssignedToIsMissing() throws Exception {
            MissionRequest invalidRequest = new MissionRequest(
                    "Misión de entrenamiento", Rank.D, null, false
            );

            performPutRequest("/missions/" + existingMissionId, invalidRequest)
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("DELETE /missions/{id} Delete Existing Missions")
    class DeleteMissionsTests {
        private Long existingMissionIdToDelete = 2L;

        @Test
        @DisplayName("Should delete an existing mission and return 200 OK with deleted mission JSON")
        void deleteMission_ReturnsOkAndDeletedMission() throws Exception {
            mockMvc.perform(get("/missions/" + existingMissionIdToDelete))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name", is("Capturar al gato del Señor Feudal")));

            performDeleteRequest("/missions/" + existingMissionIdToDelete)
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(existingMissionIdToDelete.intValue())))
                    .andExpect(jsonPath("$.name", is("Capturar al gato del Señor Feudal")))
                    .andExpect(jsonPath("$.rank", is("D")))
                    .andExpect(jsonPath("$.assignedTo", is("Naruto Uzumaki")))
                    .andExpect(jsonPath("$.completed", is(true)));

            mockMvc.perform(get("/missions/" + existingMissionIdToDelete))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 404 Not Found if mission to delete does not exist")
        void deleteMission_ReturnsNotFound_WhenIdDoesNotExist() throws Exception {
            Long nonExistentId = 999L;

            performDeleteRequest("/missions/" + nonExistentId)
                    .andExpect(status().isNotFound());
        }
    }
}
