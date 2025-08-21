package com.konoha.NinjaMissionManager.controllers;

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
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@WithMockUser
@DisplayName("VillageController Integration Tests")
@Transactional
public class VillageControllerTest {
    @Autowired
    private MockMvc mockMvc;


    @Nested
    @DisplayName("GET /villages: Retrieve all villages")
    class GetAllVillages {
        @Test
        @DisplayName("Should return all villages without filters")
        void shouldReturnAllVillages() throws Exception {
            mockMvc.perform(get("/villages")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                    .andExpect(jsonPath("$[0].name", notNullValue()));
        }

        @Test
        @DisplayName("Should return villages filtered by kage name")
        void shouldReturnVillagesFilteredByKageName() throws Exception {
            mockMvc.perform(get("/villages")
                            .param("kageName", "Tsunade")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].name", is("Konoha")))
                    .andExpect(jsonPath("$[0].kage", is("Tsunade")));
        }

        @Test
        @DisplayName("Should return empty list when no villages match the filter")
        void shouldReturnEmptyListWhenNoVillagesMatchTheFilter() throws Exception {
            mockMvc.perform(get("/villages")
                            .param("kageName", "Jiraiya")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /villages/{id}: Retrieve village by ID")
    class GetVillageById {

        @Test
        @DisplayName("Should return village by ID when it exists")
        void shouldReturnVillageById() throws Exception {
            mockMvc.perform(get("/villages/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("Konoha")))
                    .andExpect(jsonPath("$.kage", is("Tsunade")));
        }

        @Test
        @DisplayName("Should return 404 Not Found when village does not exist")
        void shouldReturnNotFoundWhenVillageDoesNotExist() throws Exception {
            mockMvc.perform(get("/villages/{id}", 999)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", containsString("Village not found")));
        }
    }
}
