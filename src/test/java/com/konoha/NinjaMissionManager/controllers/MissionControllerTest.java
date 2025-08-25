package com.konoha.NinjaMissionManager.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.konoha.NinjaMissionManager.dtos.mission.MissionCreateRequest;
import com.konoha.NinjaMissionManager.dtos.mission.MissionUpdateRequest;
import com.konoha.NinjaMissionManager.models.MissionDifficulty;
import com.konoha.NinjaMissionManager.models.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Collections;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("MissionController Integration Tests")
public class MissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("GET /missions: Retrieve all missions with filters")
    class GetAllMissions{
        @Test
        @DisplayName("Should return all missions for a Kage user without filters")
        @WithMockUser(username = "tsunade@gmail.com", roles = "KAGE")
        void shouldReturnAllMissionsAsKage() throws Exception {
            mockMvc.perform(get("/missions")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                    .andExpect(jsonPath("$[0].title", notNullValue()))
                    .andExpect(jsonPath("$[0].difficulty", notNullValue()));
        }

        @Test
        @DisplayName("Should return only assigned missions for a regular ninja user")
        @WithMockUser(username = "naruto@gmail.com", roles = "NINJA_USER")
        void shouldReturnOnlyAssignedMissionsForNinja() throws Exception {
            mockMvc.perform(get("/missions")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(7)));
        }

        @Test
        @DisplayName("Should return missions filtered by difficulty as Kage")
        @WithMockUser(username = "tsunade@gmail.com", roles = "KAGE")
        void shouldReturnMissionsFilteredByDifficulty() throws Exception {
            mockMvc.perform(get("/missions")
                            .param("difficulty", MissionDifficulty.D.name())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)))
                    .andExpect(jsonPath("$[0].difficulty", is(MissionDifficulty.D.name())));
        }

        @Test
        @DisplayName("Should return missions filtered by status as Kage")
        @WithMockUser(username = "tsunade@gmail.com", roles = "KAGE")
        void shouldReturnMissionsFilteredByStatus() throws Exception {
            mockMvc.perform(get("/missions")
                            .param("status", Status.PENDING.name())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].status", is(Status.PENDING.name())));
        }

        @Test
        @DisplayName("Should return missions filtered by assigned ninja as Kage")
        @WithMockUser(username = "tsunade@gmail.com", roles = "KAGE")
        void shouldReturnMissionsFilteredByAssignedNinja() throws Exception {
            mockMvc.perform(get("/missions")
                            .param("assignToNinjaId", "1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)));
        }
    }

    @Nested
    @DisplayName("GET /missions/{id}: Retrieve mission by ID")
    class GetMissionById {

        @Test
        @DisplayName("Should return mission for a Kage user")
        @WithMockUser(username = "tsunade@gmail.com", roles = "KAGE")
        void shouldReturnMissionForKage() throws Exception {
            mockMvc.perform(get("/missions/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.title", notNullValue()));
        }

        @Test
        @DisplayName("Should return mission for an assigned ninja")
        @WithMockUser(username = "naruto@gmail.com", roles = "NINJA_USER")
        void shouldReturnMissionForAssignedNinja() throws Exception {
            mockMvc.perform(get("/missions/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.title", notNullValue()));
        }

        @Test
        @DisplayName("Should return 403 Forbidden when a ninja tries to view an unassigned mission")
        @WithMockUser(username = "naruto@gmail.com", roles = "NINJA_USER")
        void shouldReturn403ForUnassignedMission() throws Exception {
            mockMvc.perform(get("/missions/{id}", 6)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message", containsString("permission")));
        }

        @Test
        @DisplayName("Should return 404 Not Found when mission does not exist (for Kage)")
        @WithMockUser(username = "tsunade@gmail.com", roles = "KAGE")
        void shouldReturnNotFoundWhenMissionDoesNotExist() throws Exception {
            mockMvc.perform(get("/missions/{id}", 999)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", containsString("Mission not found")));
        }
    }

    @Nested
    @DisplayName("POST /missions: Create a new mission")
    class CreateMission {

        @Test
        @DisplayName("Should create a new mission successfully as a Kage")
        @WithMockUser(username = "tsunade@gmail.com", roles = "KAGE")
        void shouldCreateMissionSuccessfully() throws Exception {
            MissionCreateRequest newMissionCreateRequest = new MissionCreateRequest(
                    "Misión de prueba exitosa",
                    "Descripción de la misión de prueba",
                    250,
                    MissionDifficulty.C,
                    Set.of(1L, 2L)
            );

            mockMvc.perform(post("/missions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newMissionCreateRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.title", is("Misión de prueba exitosa")))
                    .andExpect(jsonPath("$.difficulty", is("C")))
                    .andExpect(jsonPath("$.status", is("PENDING")))
                    .andExpect(jsonPath("$.assignedNinjas", hasSize(2)));
        }

        @Test
        @DisplayName("Should return 403 Forbidden when a non-Kage user tries to create a mission")
        @WithMockUser(username = "naruto@gmail.com", roles = "NINJA_USER")
        void shouldReturn403ForNonKageUser() throws Exception {
            MissionCreateRequest newMissionCreateRequest = new MissionCreateRequest(
                    "Misión de prueba",
                    "Descripción de la misión",
                    100,
                    MissionDifficulty.D,
                    Set.of(1L)
            );

            mockMvc.perform(post("/missions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newMissionCreateRequest)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should return 409 Conflict when a mission with the same title already exists")
        @WithMockUser(username = "tsunade@gmail.com", roles = "KAGE")
        void shouldReturn409ForDuplicateTitle() throws Exception {
            MissionCreateRequest duplicateTitleRequest = new MissionCreateRequest(
                    "Búsqueda del Gato Perdido Tora",
                    "Descripción duplicada",
                    50,
                    MissionDifficulty.D,
                    Collections.emptySet()
            );

            mockMvc.perform(post("/missions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(duplicateTitleRequest)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message", containsString("Mission with this title already exists.")));
        }

        @Test
        @DisplayName("Should return 404 Not Found when a ninja ID does not exist")
        @WithMockUser(username = "tsunade@gmail.com", roles = "KAGE")
        void shouldReturn404ForNonExistentNinja() throws Exception {
            MissionCreateRequest nonExistentNinjaRequest = new MissionCreateRequest(
                    "Misión con ninja no existente",
                    "Descripción",
                    100,
                    MissionDifficulty.C,
                    Set.of(1L, 999L)
            );

            mockMvc.perform(post("/missions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(nonExistentNinjaRequest)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", containsString("Ninja not found with id 999")));
        }

        @Test
        @DisplayName("Should return 403 Forbidden for a high-rank mission without a Jonin or Kage ninja")
        @WithMockUser(username = "tsunade@gmail.com", roles = "KAGE")
        void shouldReturn403ForHighRankMissionWithLowRankNinjas() throws Exception {
            MissionCreateRequest highRankRequest = new MissionCreateRequest(
                    "Misión de rango S peligrosa",
                    "Descripción de la misión de rango S",
                    10000,
                    MissionDifficulty.S,
                    Set.of(1L)
            );

            mockMvc.perform(post("/missions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(highRankRequest)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message", containsString("High-rank missions must be assigned to at least one Jonin or higher-rank ninja.")));
        }

        @Test
        @DisplayName("Should return 400 Bad Request when request body is invalid")
        @WithMockUser(username = "tsunade@gmail.com", roles = "KAGE")
        void shouldReturn400ForInvalidRequestBody() throws Exception {
            String invalidJson = "{ \"title\": \"\", \"description\": \"\", \"reward\": -1, \"difficulty\": \"INVALID\", \"ninjaId\": [] }";

            mockMvc.perform(post("/missions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /missions/{id}: Update a mission")
    class UpdateMission {
        @Test
        @DisplayName("Should update mission successfully as a kage")
        @WithMockUser(username = "tsunade@gmail.com", roles = "KAGE")
        void shouldUpdateMissionSuccessfullyAsKage() throws Exception {
            String newTitle = "Misión de Reconstrucción de Konoha";
            String newDescription = "Ayudar a reconstruir la aldea después de la invasión.";
            MissionUpdateRequest updateRequest = new MissionUpdateRequest(
                    newTitle,
                    newDescription,
                    500,
                    MissionDifficulty.B,
                    Status.ACTIVE,
                    Set.of(1L, 2L)
            );

            mockMvc.perform(put("/missions/{id}", 7)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(7)))
                    .andExpect(jsonPath("$.title", is(newTitle)))
                    .andExpect(jsonPath("$.status", is("ACTIVE")))
                    .andExpect(jsonPath("$.assignedNinjas", hasSize(2)));
        }

        @Test
        @DisplayName("Should return 409 Conflict when a Kage tries to update with a duplicate title")
        @WithMockUser(username = "tsunade@gmail.com", roles = "KAGE")
        void shouldReturn409ForDuplicateTitleAsKage() throws Exception {
            MissionUpdateRequest updateRequest = new MissionUpdateRequest(
                    "Búsqueda del Gato Perdido Tora",
                    null, null, null, Status.ACTIVE, null
            );

            mockMvc.perform(put("/missions/{id}", 7)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message", containsString("Mission with this title already exists.")));
        }

        @Test
        @DisplayName("Should return 403 Forbidden for a Kage updating a high-rank mission with only low-rank ninjas")
        @WithMockUser(username = "tsunade@gmail.com", roles = "KAGE")
        void shouldReturn403ForHighRankMissionAsKage() throws Exception {
            MissionUpdateRequest updateRequest = new MissionUpdateRequest(
                    null, null, null, MissionDifficulty.S, Status.ACTIVE, Set.of(1L, 2L)
            );

            mockMvc.perform(put("/missions/{id}", 4)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message", containsString("High-rank missions must be assigned to at least one Jonin or higher-rank ninja.")));
        }

        @Test
        @DisplayName("Should update mission status successfully as an assigned ninja")
        @WithMockUser(username = "naruto@gmail.com", roles = "NINJA_USER")
        void shouldUpdateMissionStatusAsAssignedNinja() throws Exception {
            MissionUpdateRequest updateRequest = new MissionUpdateRequest(
                    null, null, null, null, Status.ACTIVE, null
            );

            mockMvc.perform(put("/missions/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", is("ACTIVE")));
        }

        @Test
        @DisplayName("Should return 403 Forbidden when an unassigned ninja tries to update a mission")
        @WithMockUser(username = "kakashi@gmail.com", roles = "NINJA_USER")
        void shouldReturn403ForUnassignedNinja() throws Exception {
            MissionUpdateRequest updateRequest = new MissionUpdateRequest(
                    null, null, null, null, Status.ACTIVE, Set.of(1L)
            );

            mockMvc.perform(put("/missions/{id}", 7)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message", containsString("permission")));
        }

        @Test
        @DisplayName("Should return 403 Forbidden when a ninja tries to update a field other than status")
        @WithMockUser(username = "naruto@gmail.com", roles = "NINJA_USER")
        void shouldReturn403WhenNinjaUpdatesOtherField() throws Exception {
            MissionUpdateRequest updateRequest = new MissionUpdateRequest(
                    "Nuevo Título", null, null, null, Status.ACTIVE, null
            );

            mockMvc.perform(put("/missions/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.message", containsString("Only the mission status can be updated by a assigned ninja.")));
        }

        @Test
        @DisplayName("Should return 404 Not Found when updating a non-existent mission")
        @WithMockUser(username = "tsunade@gmail.com", roles = "KAGE")
        void shouldReturn404ForNonExistentMission() throws Exception {
            MissionUpdateRequest updateRequest = new MissionUpdateRequest(
                    "Infiltración en la Aldea de la Lluvia", "Jiraiya se infiltra...", 50000, MissionDifficulty.S, Status.COMPLETED, Set.of()
            );

            mockMvc.perform(put("/missions/{id}", 999)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", containsString("Mission not found with ID: 999")));
        }

        @Test
        @DisplayName("Should return 400 Bad Request when request body is invalid")
        @WithMockUser(username = "tsunade@gmail.com", roles = "KAGE")
        void shouldReturn400ForInvalidRequestBody() throws Exception {
            String invalidJson = "{ \"title\": \"\", \"difficulty\": \"INVALID\", \"reward\": -1 }";

            mockMvc.perform(put("/missions/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest());
        }
    }
}

