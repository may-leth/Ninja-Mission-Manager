package com.konoha.NinjaMissionManager.controllers;

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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("MissionController Integration Tests")
public class MissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
}

