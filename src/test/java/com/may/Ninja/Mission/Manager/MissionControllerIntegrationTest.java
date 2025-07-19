package com.may.Ninja.Mission.Manager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class MissionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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
    void getMissionById_returnsMission() throws Exception{
        mockMvc.perform(get("/missions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Proteger al constructor de puentes Tazuna")))
                .andExpect(jsonPath("$.rank", is("C")))
                .andExpect(jsonPath("$.completed", is(false)));
    }


    @Test
    @DisplayName("GET /missions/{id} should return 404 when not found")
    void getMissionById_notFound() throws Exception {
        mockMvc.perform(get("/missions/99"))
                .andExpect(status().isNotFound());
    }
}
