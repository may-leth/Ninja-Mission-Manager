package com.konoha.NinjaMissionManager.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.konoha.NinjaMissionManager.dtos.village.VillageRequest;
import com.konoha.NinjaMissionManager.dtos.village.VillageUpdateRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("VillageController Integration Tests")
@Transactional
public class VillageControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Nested
    @WithMockUser
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
    @WithMockUser
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

    @Nested
    @DisplayName("POST /villages: Create a new village")
    class CreateVillage {
        @Test
        @WithMockUser(username = "tsunade@gmail.com", roles = "KAGE")
        @DisplayName("Should create a new village successfully as a kage")
        void shouldCreateNewVillageAsKage() throws Exception {
            VillageRequest newVillage = new VillageRequest("VillageHiddenInTheClouds", 3L);

            mockMvc.perform(post("/villages")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newVillage)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name", is(newVillage.name())))
                    .andExpect(jsonPath("$.kage", is("Kakashi Hatake")));
        }

        @Test
        @WithMockUser(username = "tsunade@gmail.com", roles = "KAGE")
        @DisplayName("Should return 409 Conflict when a village with the same name already exists")
        void shouldReturnConflictWhenVillageNameExists() throws Exception {
            VillageRequest newVillage = new VillageRequest("Konoha", 3L);

            mockMvc.perform(post("/villages")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newVillage)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message", containsString("Village with this name already exists")));
        }

        @Test
        @WithMockUser(username = "tsunade@gmail.com", roles = "KAGE")
        @DisplayName("Should return 409 Conflict when the Kage is already leading another village")
        void shouldReturnConflictWhenKageAlreadyHasAVillage() throws Exception {
            VillageRequest newVillage = new VillageRequest("VillageOfRain", 4L);

            mockMvc.perform(post("/villages")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newVillage)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message", containsString("is already the Kage of another village")));
        }

        @Test
        @WithMockUser(username = "tsunade@gmail.com", roles = "KAGE")
        @DisplayName("Should return 404 Not Found when Kage ID does not exist")
        void shouldReturnNotFoundWhenKageIdDoesNotExist() throws Exception {
            VillageRequest newVillage = new VillageRequest("VillageOfSound", 999L);

            mockMvc.perform(post("/villages")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newVillage)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", is("Ninja not found with id 999")));
        }

        @Test
        @WithMockUser(username = "naruto@gmail.com", roles = "NINJA_USER")
        @DisplayName("Should return 403 Forbidden when a user without Kage role tries to create a village")
        void shouldReturnForbiddenForNonKageUser() throws Exception {
            VillageRequest newVillage = new VillageRequest("VillageOfSound", 1L);

            mockMvc.perform(post("/villages")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newVillage)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("PUT /villages/{id}: update village")
    class UpdateVillage {
        @Test
        @DisplayName("Should update village successfully (name and kage)")
        @WithMockUser(username = "tsunade@gmail.com", roles = "KAGE")
        void shouldUpdateVillageSuccessfully() throws Exception{
            VillageUpdateRequest request = new VillageUpdateRequest("Konohagakure", 6L);

            mockMvc.perform(put("/villages/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("Konohagakure")))
                    .andExpect(jsonPath("$.kage", is("Itachi Uchiha")));
        }

        @Test
        @DisplayName("Should update village successfully (only name)")
        @WithMockUser(username = "tsunade@gmail.com", roles = "KAGE")
        void shouldUpdateOnlyNameSuccessfully() throws Exception {
            VillageUpdateRequest request = new VillageUpdateRequest("Shinobi no Kuni", null);

            mockMvc.perform(put("/villages/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("Shinobi no Kuni")))
                    .andExpect(jsonPath("$.kage", is("Tsunade")));
        }

        @Test
        @DisplayName("Should update village successfully (only kage)")
        @WithMockUser(username = "tsunade@gmail.com", roles = "KAGE")
        void shouldUpdateOnlyKageSuccessfully() throws Exception {
            VillageUpdateRequest request = new VillageUpdateRequest(null, 6L);

            mockMvc.perform(put("/villages/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("Konoha")))
                    .andExpect(jsonPath("$.kage", is("Itachi Uchiha")));
        }

        @Test
        @DisplayName("Should update village successfully when kage is the same")
        @WithMockUser(username = "tsunade@gmail.com", roles = "KAGE")
        void shouldUpdateVillageWhenKageIsTheSame() throws Exception {
            VillageUpdateRequest request = new VillageUpdateRequest("Konoha", 5L);

            mockMvc.perform(put("/villages/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.kage", is("Tsunade")));
        }

        @Test
        @DisplayName("Should return 403 Forbidden for a non-Kage user")
        @WithMockUser(username = "naruto@gmail.com", roles = "NINJA_USER")
        void shouldReturn403ForNonKageUser() throws Exception {
            VillageUpdateRequest request = new VillageUpdateRequest("Test", null);

            mockMvc.perform(put("/villages/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Should return 404 Not Found when village does not exist")
        @WithMockUser(username = "tsunade@gmail.com", roles = "KAGE")
        void shouldReturn404WhenVillageDoesNotExist() throws Exception {
            VillageUpdateRequest request = new VillageUpdateRequest("Test", null);

            mockMvc.perform(put("/villages/{id}", 999)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", containsString("Village not found")));
        }

        @Test
        @DisplayName("Should return 409 Conflict when new village name already exists")
        @WithMockUser(username = "tsunade@gmail.com", roles = "KAGE")
        void shouldReturn409WhenVillageNameExists() throws Exception {
            VillageUpdateRequest request = new VillageUpdateRequest("Suna", null);

            mockMvc.perform(put("/villages/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message", containsString("Village with this name already exists")));
        }

        @Test
        @DisplayName("Should return 404 Not Found when new kage does not exist")
        @WithMockUser(username = "tsunade@gmail.com", roles = "KAGE")
        void shouldReturn404WhenNewKageDoesNotExist() throws Exception {
            VillageUpdateRequest request = new VillageUpdateRequest(null, 999L);

            mockMvc.perform(put("/villages/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", containsString("Ninja not found")));
        }

        @Test
        @DisplayName("Should return 409 Conflict when new kage already leads another village")
        @WithMockUser(username = "tsunade@gmail.com", roles = "KAGE")
        void shouldReturn409WhenNewKageAlreadyLeadsAnotherVillage() throws Exception {
            VillageUpdateRequest request = new VillageUpdateRequest(null, 4L);

            mockMvc.perform(put("/villages/{id}", 1)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message", containsString("is already the Kage of another village.")));
        }
    }
}
