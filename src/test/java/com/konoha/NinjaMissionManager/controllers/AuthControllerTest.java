package com.konoha.NinjaMissionManager.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.konoha.NinjaMissionManager.dtos.ninja.KageCreateNinjaRequest;
import com.konoha.NinjaMissionManager.dtos.ninja.NinjaLoginRequest;
import com.konoha.NinjaMissionManager.dtos.ninja.NinjaRegisterRequest;
import com.konoha.NinjaMissionManager.models.Rank;
import com.konoha.NinjaMissionManager.models.Role;
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

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("AuthController Integration Tests")
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private NinjaRegisterRequest createNinjaRegisterRequest() {
        return new NinjaRegisterRequest("Nuevo Ninja", "nuevo.ninja@test.com", "Password123#", 1L);
    }

    private NinjaLoginRequest createValidLoginRequest() {
        return new NinjaLoginRequest("naruto@gmail.com", "Naruto12345.");
    }

    private String getAuthToken(String email, String password) throws Exception {
        NinjaLoginRequest loginRequest = new NinjaLoginRequest(email, password);
        String response = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("jwt").asText();
    }

    @Nested
    @DisplayName("POST /register: Register new ninja")
    class RegisterNewNinja {

        @Test
        @DisplayName("Should register a new ninja and return CREATED status")
        void shouldRegisterNewNinjaSuccessfully() throws Exception {
            NinjaRegisterRequest request = createNinjaRegisterRequest();

            mockMvc.perform(post("/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value(request.name()))
                    .andExpect(jsonPath("$.email").value(request.email()))
                    .andExpect(jsonPath("$.village").value("Konoha"))
                    .andExpect(jsonPath("$.rank").value("GENIN"));
        }

        @Test
        @DisplayName("Should return BAD_REQUEST status when email is invalid")
        void shouldReturnBadRequestWhenEmailIsInvalid() throws Exception {
            NinjaRegisterRequest request = new NinjaRegisterRequest(
                    "Test Ninja",
                    "invalid-email",
                    "Password123#",
                    1L
            );

            mockMvc.perform(post("/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Email not valid"));
        }

        @Test
        @DisplayName("Should return CONFLICT status when email is already registered")
        void shouldFailWhenEmailExists() throws Exception{
            NinjaRegisterRequest request = new NinjaRegisterRequest(
                    "Nuevo Naruto",
                    "naruto@gmail.com",
                    "Password123#",
                    1L);

            mockMvc.perform(post("/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("Email is already registered: naruto@gmail.com"));
        }

        @Test
        @DisplayName("Should fail when password is invalid")
        void shouldFailInvalidPassword() throws Exception {
            NinjaRegisterRequest request = new NinjaRegisterRequest(
                    "Naruto",
                    "naruto@gmail.com",
                    "12345",
                    1L
            );

            mockMvc.perform(post("/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Password must contain a minimum of 8 characters, including a number, one uppercase letter, one lowercase letter and one special character"));
        }
    }

    @Nested
    @DisplayName("POST /register/kage: Kage registers a ninja")
    class KageRegisterNinja{

        @Test
        @WithMockUser(roles = "KAGE", username = "tsunade@gmail.com")
        @DisplayName("Kage registers ninja successfully")
        void shouldRegisterNinjaAsKage() throws Exception{
            KageCreateNinjaRequest request = new KageCreateNinjaRequest(
                    "Nuevo Kage Ninja",
                    "kage.ninja@test.com",
                    "Password123#",
                    Rank.JONIN,
                    1L,
                    true,
                    Set.of(Role.ROLE_NINJA_USER, Role.ROLE_ANBU)
            );

            mockMvc.perform(post("/register/kage")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value(request.name()));
        }

        @Test
        @WithMockUser(roles = "NINJA_USER", username = "naruto@gmail.com")
        @DisplayName("Should return FORBIDDEN status when a regular user tries to access kage endpoint")
        void shouldReturnForbiddenStatusWhenUserIsNotKage() throws Exception {
            KageCreateNinjaRequest kageRequest = new KageCreateNinjaRequest(
                    "Hokage Minato",
                    "minato@konoha.com",
                    "Password123#",
                    Rank.KAGE,
                    1L,
                    false,
                    Set.of(Role.ROLE_KAGE)
            );

            mockMvc.perform(post("/register/kage")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(kageRequest)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "KAGE")
        @DisplayName("Should fail when rank is null")
        void shouldFailNullRank() throws Exception {
            KageCreateNinjaRequest request = new KageCreateNinjaRequest(
                    "Sasuke",
                    "sasuke@konoha.com",
                    "S@suke123",
                    null,
                    1L,
                    true,
                    Set.of(Role.ROLE_NINJA_USER)
            );

            mockMvc.perform(post("/register/kage")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Rank is required"));
        }
    }

    @Nested
    @DisplayName("POST /login: Authenticate ninja")
    class LoginNinja {

        @Test
        @DisplayName("Should return a JWT token on successful login")
        void shouldReturnTokenForValidCredentials() throws Exception {
            NinjaLoginRequest request = createValidLoginRequest();

            mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").isNotEmpty());
        }

        @Test
        @DisplayName("Should return FORBIDDEN status on invalid login credentials")
        void shouldReturnUnauthorizedForInvalidCredentials() throws Exception {
            NinjaLoginRequest request = new NinjaLoginRequest(
                    "naruto@gmail.com",
                    "Wrongpassword12345."
            );

            mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }
}
