package com.konoha.NinjaMissionManager.services;

import com.konoha.NinjaMissionManager.dtos.mission.MissionMapper;
import com.konoha.NinjaMissionManager.dtos.ninja.NinjaMapper;
import com.konoha.NinjaMissionManager.dtos.ninja.NinjaResponse;
import com.konoha.NinjaMissionManager.exceptions.ResourceNotFoundException;
import com.konoha.NinjaMissionManager.models.Ninja;
import com.konoha.NinjaMissionManager.models.Rank;
import com.konoha.NinjaMissionManager.repositories.NinjaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for NinjaServices")
public class NinjaServiceTest {
    @Mock
    private NinjaRepository ninjaRepository;

    @Mock
    private NinjaMapper ninjaMapper;

    @Mock
    private MissionMapper missionMapper;

    @InjectMocks
    private NinjaService ninjaService;

    private Ninja ninja;
    private NinjaResponse ninjaResponse;

    @BeforeEach
    void setUp() {
        ninja = new Ninja();
        ninja.setId(1L);
        ninja.setName("Kakashi Hatake");

        ninjaResponse = new NinjaResponse(
                1L,
                "Kakashi Hatake",
                "kakashi@leaf.com",
                "JONIN",
                "konoha",
                50,
                false,
                Set.of()
        );
    }

    @Nested
    @DisplayName("getAllNinjas")
    class GetAll {

        @Test
        @DisplayName("Should return all ninjas without filters")
        void shouldReturnAllWithoutFilters() {
            when(ninjaRepository.findAll(any(Specification.class))).thenReturn(List.of(ninja));
            when(ninjaMapper.entityToDto(ninja, missionMapper)).thenReturn(ninjaResponse);

            List<NinjaResponse> result = ninjaService.getAllNinjas(Optional.empty(), Optional.empty(), Optional.empty());

            assertThat(result).hasSize(1).containsExactly(ninjaResponse);
            verify(ninjaRepository).findAll(any(Specification.class));
            verify(ninjaMapper).entityToDto(ninja, missionMapper);
        }

        @Test
        @DisplayName("Should return ninjas filtered by rank")
        void shouldReturnFilteredByRank() {
            when(ninjaRepository.findAll(any(Specification.class))).thenReturn(List.of(ninja));
            when(ninjaMapper.entityToDto(ninja, missionMapper)).thenReturn(ninjaResponse);

            List<NinjaResponse> result = ninjaService.getAllNinjas(
                    Optional.of(Rank.GENIN),
                    Optional.empty(),
                    Optional.empty());

            assertThat(result).hasSize(1).containsExactly(ninjaResponse);
            verify(ninjaRepository).findAll(any(Specification.class));
        }

        @Test
        @DisplayName("Should return ninjas filtered by village")
        void shouldReturnFilteredByVillage() {
            when(ninjaRepository.findAll(any(Specification.class))).thenReturn(List.of(ninja));
            when(ninjaMapper.entityToDto(ninja, missionMapper)).thenReturn(ninjaResponse);

            List<NinjaResponse> result = ninjaService.getAllNinjas(
                    Optional.empty(),
                    Optional.of(1L),
                    Optional.empty());

            assertThat(result).hasSize(1).containsExactly(ninjaResponse);
            verify(ninjaRepository).findAll(any(Specification.class));
        }

        @Test
        @DisplayName("Should return ninjas filtered by if is Anbu")
        void shouldReturnFilteredByIsAnbu() {
            when(ninjaRepository.findAll(any(Specification.class))).thenReturn(List.of(ninja));
            when(ninjaMapper.entityToDto(ninja, missionMapper)).thenReturn(ninjaResponse);

            List<NinjaResponse> result = ninjaService.getAllNinjas(
                    Optional.empty(),
                    Optional.empty(),
                    Optional.of(true));

            assertThat(result).hasSize(1).containsExactly(ninjaResponse);
            verify(ninjaRepository).findAll(any(Specification.class));
        }

        @Test
        @DisplayName("Should return empty list when no results match filters")
        void shouldReturnEmptyWhenNoMatch() {
            when(ninjaRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

            List<NinjaResponse> result = ninjaService.getAllNinjas(
                    Optional.of(Rank.GENIN),
                    Optional.of(99L),
                    Optional.of(false));

            assertThat(result).isEmpty();
            verify(ninjaRepository).findAll(any(Specification.class));
            verifyNoInteractions(ninjaMapper);
        }
    }


    @Nested
    @DisplayName("getNinjaById")
    class GetNinjaByIdTests {

        @Test
        @DisplayName("Should return ninja when id exists")
        void shouldReturnNinjaWhenIdExists() {
            when(ninjaRepository.findById(1L)).thenReturn(Optional.of(ninja));
            when(ninjaMapper.entityToDto(ninja, missionMapper)).thenReturn(ninjaResponse);

            NinjaResponse result = ninjaService.getNinjaById(1L);

            assertThat(result).isEqualTo(ninjaResponse);
            verify(ninjaRepository).findById(1L);
            verify(ninjaMapper).entityToDto(ninja, missionMapper);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when id does not exist")
        void shouldThrowExceptionWhenIdNotFound() {
            when(ninjaRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> ninjaService.getNinjaById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Ninja not found with ID: 99");

            verify(ninjaRepository).findById(99L);
            verifyNoInteractions(ninjaMapper);
        }
    }
}
