package com.konoha.NinjaMissionManager.services;

import com.konoha.NinjaMissionManager.dtos.mission.MissionMapper;
import com.konoha.NinjaMissionManager.dtos.mission.MissionRequest;
import com.konoha.NinjaMissionManager.dtos.mission.MissionResponse;
import com.konoha.NinjaMissionManager.dtos.mission.MissionSummaryResponse;
import com.konoha.NinjaMissionManager.exceptions.ResourceConflictException;
import com.konoha.NinjaMissionManager.exceptions.ResourceNotFoundException;
import com.konoha.NinjaMissionManager.models.*;
import com.konoha.NinjaMissionManager.repositories.MissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit tests for MissionService")
public class MissionServiceTest {
    @Mock
    private MissionRepository missionRepository;

    @Mock
    private MissionMapper missionMapper;

    @Mock
    private NinjaService ninjaService;

    @Mock
    private Principal principal;

    @InjectMocks
    private MissionService missionService;

    private Ninja naruto;
    private Ninja sasuke;
    private Ninja kage;
    private Mission missionA;
    private Mission missionB;
    private MissionRequest missionRequest;
    private MissionResponse missionAResponse;
    private MissionSummaryResponse missionASummaryResponse;


    @BeforeEach
    void setUp() {
        naruto = new Ninja(1L, "Naruto Uzumaki", "naruto@gmail.com", "Naruto12345.", Rank.GENIN, new Village(), 0, false, Set.of(Role.ROLE_NINJA_USER), Collections.emptySet());
        sasuke = new Ninja(2L, "Sasuke Uchiha", "sasuke@gmail.com", "Sasuke12345.", Rank.JONIN, new Village(), 0, false, Set.of(Role.ROLE_NINJA_USER), Collections.emptySet());
        kage = new Ninja(3L, "Tsunade Senju", "tsunade@gmail.com", "Tsunade12345.", Rank.KAGE, new Village(), 0, false, Set.of(Role.ROLE_KAGE), Collections.emptySet());

        missionA = new Mission(1L, "Misión de limpieza", "Limpia la propiedad del señor feudal", 50, MissionDifficulty.D, Status.COMPLETED, LocalDateTime.now(), Set.of(naruto, sasuke));
        missionB = new Mission(2L, "Captura del Jinchuriki", "Capturar a Killer B", 5000, MissionDifficulty.A, Status.COMPLETED, LocalDateTime.of(2025, 8, 24, 10, 0), Collections.emptySet());

        missionRequest = new MissionRequest("Misión de protección", "Proteger al señor feudal", 500, MissionDifficulty.B, Set.of(naruto.getId()));
        missionAResponse = new MissionResponse(1L,"Misión de limpieza","Limpia la propiedad del señor feudal", 50, MissionDifficulty.D, Status.COMPLETED, LocalDateTime.now(), Collections.emptySet());
        missionASummaryResponse = new MissionSummaryResponse(1L,"Misión de limpieza", MissionDifficulty.D, Status.COMPLETED);

        naruto.setAssignedMissions(Set.of(missionA));
        sasuke.setAssignedMissions(Set.of(missionA));
        kage.setAssignedMissions(Set.of(missionA, missionB));
    }

    @Nested
    @DisplayName("getAllMissions")
    class GetAllMissionsTests{
        @Test
        @DisplayName("Should return all missions for a kage without filters")
        void shouldReturnAllMissionsForKage(){
            when(ninjaService.getAuthenticatedNinja(principal)).thenReturn(kage);
            when(missionRepository.findAll(any(Specification.class))).thenReturn(List.of(missionA, missionB));
            when(missionMapper.entityToSummaryDto(missionA)).thenReturn(missionASummaryResponse);
            when(missionMapper.entityToSummaryDto(missionB)).thenReturn(new MissionSummaryResponse(2L, "Captura del Jinchuriki", MissionDifficulty.A, Status.COMPLETED));

            List<MissionSummaryResponse> result = missionService.getAllMissions(Optional.empty(), Optional.empty(), Optional.empty(), principal);

            assertThat(result).hasSize(2);
            verify(missionRepository).findAll(any(Specification.class));
            verify(missionMapper, times(2)).entityToSummaryDto(any(Mission.class));
        }

        @Test
        @DisplayName("Should return only assigned missions for a non-Kage ninja")
        void shouldReturnOnlyAssignedMissionsForNinja() {
            when(ninjaService.getAuthenticatedNinja(principal)).thenReturn(naruto);
            when(missionRepository.findAll(any(Specification.class))).thenReturn(List.of(missionA));
            when(missionMapper.entityToSummaryDto(missionA)).thenReturn(missionASummaryResponse);

            List<MissionSummaryResponse> result = missionService.getAllMissions(Optional.empty(), Optional.empty(), Optional.empty(), principal);

            assertThat(result).hasSize(1).containsExactly(missionASummaryResponse);
            verify(missionRepository).findAll(any(Specification.class));
            verify(missionMapper).entityToSummaryDto(missionA);
        }

        @Test
        @DisplayName("Should return missions filtered by difficulty for a Kage")
        void shouldReturnMissionsFilteredByDifficulty() {
            when(ninjaService.getAuthenticatedNinja(principal)).thenReturn(kage);
            when(missionRepository.findAll(any(Specification.class))).thenReturn(List.of(missionB));
            when(missionMapper.entityToSummaryDto(missionB)).thenReturn(new MissionSummaryResponse(2L, "Captura del Jinchuriki", MissionDifficulty.A, Status.COMPLETED));

            List<MissionSummaryResponse> result = missionService.getAllMissions(Optional.of(MissionDifficulty.A), Optional.empty(), Optional.empty(), principal);

            assertThat(result).hasSize(1);
            verify(missionRepository).findAll(any(Specification.class));
        }

        @Test
        @DisplayName("Should return missions filtered by status and assigned ninja")
        void shouldReturnMissionsFilteredByStatusAndAssignedNinja() {
            when(ninjaService.getAuthenticatedNinja(principal)).thenReturn(kage);
            when(missionRepository.findAll(any(Specification.class))).thenReturn(List.of(missionA));
            when(missionMapper.entityToSummaryDto(missionA)).thenReturn(missionASummaryResponse);

            List<MissionSummaryResponse> result = missionService.getAllMissions(Optional.empty(), Optional.of(Status.COMPLETED), Optional.of(naruto.getId()), principal);

            assertThat(result).hasSize(1).containsExactly(missionASummaryResponse);
            verify(missionRepository).findAll(any(Specification.class));
        }

        @Test
        @DisplayName("Should return empty list when no missions match filters")
        void shouldReturnEmptyListWhenNoMissionsMatch() {
            when(ninjaService.getAuthenticatedNinja(principal)).thenReturn(kage);
            when(missionRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

            List<MissionSummaryResponse> result = missionService.getAllMissions(Optional.of(MissionDifficulty.S), Optional.empty(), Optional.empty(), principal);

            assertThat(result).isEmpty();
            verify(missionRepository).findAll(any(Specification.class));
            verifyNoInteractions(missionMapper);
        }
    }

    @Nested
    @DisplayName("getMissionById")
    class GetMissionByIdTests {

        @Test
        @DisplayName("Should return mission for a Kage")
        void shouldReturnMissionForKage() {
            when(ninjaService.getAuthenticatedNinja(principal)).thenReturn(kage);
            when(missionRepository.findById(missionA.getId())).thenReturn(Optional.of(missionA));
            when(missionMapper.entityToDto(missionA)).thenReturn(missionAResponse);

            MissionResponse result = missionService.getMissionById(missionA.getId(), principal);

            assertThat(result).isEqualTo(missionAResponse);
            verify(missionRepository).findById(missionA.getId());
            verify(missionMapper).entityToDto(missionA);
        }

        @Test
        @DisplayName("Should return mission for an assigned ninja")
        void shouldReturnMissionForAssignedNinja() {
            when(ninjaService.getAuthenticatedNinja(principal)).thenReturn(naruto);
            when(missionRepository.findById(missionA.getId())).thenReturn(Optional.of(missionA));
            when(missionMapper.entityToDto(missionA)).thenReturn(missionAResponse);

            MissionResponse result = missionService.getMissionById(missionA.getId(), principal);

            assertThat(result).isEqualTo(missionAResponse);
            verify(missionRepository).findById(missionA.getId());
            verify(missionMapper).entityToDto(missionA);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when mission does not exist")
        void shouldThrowExceptionWhenMissionNotFound() {
            when(ninjaService.getAuthenticatedNinja(principal)).thenReturn(kage);
            when(missionRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> missionService.getMissionById(99L, principal))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Mission not found with ID: 99");

            verify(missionRepository).findById(99L);
            verifyNoInteractions(missionMapper);
        }

        @Test
        @DisplayName("Should throw AccessDeniedException when ninja is not assigned to mission")
        void shouldThrowAccessDeniedWhenNinjaIsNotAssigned() {
            when(ninjaService.getAuthenticatedNinja(principal)).thenReturn(sasuke);
            when(missionRepository.findById(missionB.getId())).thenReturn(Optional.of(missionB));

            assertThatThrownBy(() -> missionService.getMissionById(missionB.getId(), principal))
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessageContaining("You do not have permission to view this mission.");

            verify(missionRepository).findById(missionB.getId());
            verifyNoInteractions(missionMapper);
        }
    }

    @Nested
    @DisplayName("createMission")
    class CreateMissionTests {

        @Test
        @DisplayName("Should create a mission successfully as a Kage")
        void shouldCreateMissionAsKage() {
            when(ninjaService.getAuthenticatedNinja(principal)).thenReturn(kage);
            when(missionRepository.existsByTitle(anyString())).thenReturn(false);
            when(ninjaService.getNinjaEntityById(naruto.getId())).thenReturn(naruto);
            when(missionRepository.save(any(Mission.class))).thenReturn(missionA);
            when(missionMapper.entityToDto(any(Mission.class))).thenReturn(missionAResponse);

            MissionResponse result = missionService.createMission(missionRequest, principal);

            assertThat(result).isEqualTo(missionAResponse);
            verify(ninjaService).getAuthenticatedNinja(principal);
            verify(missionRepository).existsByTitle(missionRequest.title());
            verify(ninjaService).getNinjaEntityById(naruto.getId());
            verify(missionRepository).save(any(Mission.class));
            verify(missionMapper).entityToDto(any(Mission.class));
        }

        @Test
        @DisplayName("Should throw AccessDeniedException for a non-Kage ninja")
        void shouldThrowAccessDeniedForNonKage() {
            when(ninjaService.getAuthenticatedNinja(principal)).thenReturn(naruto);

            assertThatThrownBy(() -> missionService.createMission(missionRequest, principal))
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessageContaining("Only a Kage can create or manage missions.");

            verify(ninjaService).getAuthenticatedNinja(principal);
            verify(missionRepository, never()).existsByTitle(anyString());
        }

        @Test
        @DisplayName("Should throw ResourceConflictException for a duplicate title")
        void shouldThrowConflictForDuplicateTitle() {
            when(ninjaService.getAuthenticatedNinja(principal)).thenReturn(kage);
            when(missionRepository.existsByTitle(anyString())).thenReturn(true);

            assertThatThrownBy(() -> missionService.createMission(missionRequest, principal))
                    .isInstanceOf(ResourceConflictException.class)
                    .hasMessageContaining("Mission with this title already exists.");

            verify(ninjaService).getAuthenticatedNinja(principal);
            verify(missionRepository).existsByTitle(missionRequest.title());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException for a non-existent ninja ID")
        void shouldThrowNotFoundForNonExistentNinja() {
            MissionRequest badRequest = new MissionRequest("Misión de entrenamiento", "Entrenamiento de combate", 50, MissionDifficulty.D, Set.of(999L));

            when(ninjaService.getAuthenticatedNinja(principal)).thenReturn(kage);
            when(missionRepository.existsByTitle(anyString())).thenReturn(false);
            when(ninjaService.getNinjaEntityById(999L)).thenThrow(new ResourceNotFoundException("Ninja not found with ID: 999"));

            assertThatThrownBy(() -> missionService.createMission(badRequest, principal))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Ninja not found with ID: 999");

            verify(ninjaService).getAuthenticatedNinja(principal);
            verify(missionRepository).existsByTitle(badRequest.title());
        }

        @Test
        @DisplayName("Should throw AccessDeniedException for a high-rank mission without a Jonin or higher ninja")
        void shouldThrowAccessDeniedForHighRankMission() {
            MissionRequest highRankRequest = new MissionRequest("Misión S", "Misión de alto rango", 5000, MissionDifficulty.S, Set.of(naruto.getId()));

            when(ninjaService.getAuthenticatedNinja(principal)).thenReturn(kage);
            when(missionRepository.existsByTitle(anyString())).thenReturn(false);
            when(ninjaService.getNinjaEntityById(naruto.getId())).thenReturn(naruto);

            assertThatThrownBy(() -> missionService.createMission(highRankRequest, principal))
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessageContaining("High-rank missions must be assigned to at least one Jonin or higher-rank ninja.");

            verify(ninjaService).getAuthenticatedNinja(principal);
            verify(missionRepository).existsByTitle(highRankRequest.title());
            verify(ninjaService).getNinjaEntityById(naruto.getId());
        }
    }
}
