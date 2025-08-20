package com.konoha.NinjaMissionManager.controllers;

import com.konoha.NinjaMissionManager.models.Rank;
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
@DisplayName("NinjaController Integration Tests")
class NinjaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("GET /ninjas: Retrieve all ninjas")
    class GetAllNinjas {

        @Test
        @DisplayName("Should return 403 Forbidden for a regular user")
        @WithMockUser(roles = "NINJA_USER")
        void shouldReturn403ForRegularUser() throws Exception {
            mockMvc.perform(get("/ninjas")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should return all ninjas without filters when user is Kage")
        @WithMockUser(roles = "KAGE", username = "tsunade@gmail.com")
        void shouldReturnAllNinjasAsKage() throws Exception {
            mockMvc.perform(get("/ninjas")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                    .andExpect(jsonPath("$[0].name", notNullValue()))
                    .andExpect(jsonPath("$[0].rank", notNullValue()));
        }

        @Test
        @DisplayName("Should return ninjas filtered by rank when user is Kage")
        @WithMockUser(roles = "KAGE", username = "tsunade@gmail.com")
        void shouldReturnNinjasFilteredByRankAsKage() throws Exception {
            mockMvc.perform(get("/ninjas")
                            .param("rank", Rank.GENIN.name())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", everyItem(hasEntry("rank", Rank.GENIN.name()))));
        }
    }

    @Nested
    @DisplayName("GET /ninjas/{id}: Retrieve ninja by ID")
    class GetNinjaById {

        @Test
        @DisplayName("Should return ninja for authenticated user")
        @WithMockUser(username = "kakashi@gmail.com")
        void shouldReturnNinjaForOwner() throws Exception {
            mockMvc.perform(get("/ninjas/{id}", 3)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name", notNullValue()))
                    .andExpect(jsonPath("$.rank", notNullValue()));
        }

        @Test
        @DisplayName("Should return ninja when user is a Kage")
        @WithMockUser(roles = "KAGE", username = "tsunade@gmail.com")
        void shouldReturnNinjaForKage() throws Exception {
            mockMvc.perform(get("/ninjas/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name", notNullValue()))
                    .andExpect(jsonPath("$.rank", notNullValue()));
        }

        @Test
        @DisplayName("Should return 403 Forbidden when a user tries to view another ninja's data")
        @WithMockUser(username = "naruto@gmail.com")
        void shouldReturn403ForOtherNinja() throws Exception {
            mockMvc.perform(get("/ninjas/{id}", 2)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should return 404 Not Found when ninja does not exist (for Kage)")
        @WithMockUser(roles = "KAGE", username = "tsunade@konoha.com")
        void shouldReturnNotFoundWhenNinjaDoesNotExist() throws Exception {
            mockMvc.perform(get("/ninjas/{id}", 999)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", containsString("Ninja not found")));
        }
    }
}

