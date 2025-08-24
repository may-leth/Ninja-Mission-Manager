package com.konoha.NinjaMissionManager.services;

import com.konoha.NinjaMissionManager.dtos.mission.MissionMapper;
import com.konoha.NinjaMissionManager.dtos.mission.MissionResponse;
import com.konoha.NinjaMissionManager.dtos.mission.MissionSummaryResponse;
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
    private MissionResponse missionAResponse;
    private MissionSummaryResponse missionASummaryResponse;

    @BeforeEach
    void setUp() {
        naruto = new Ninja(1L, "Naruto Uzumaki", "naruto@gmail.com", "Naruto12345.", Rank.GENIN, new Village(), 0, false, Set.of(Role.ROLE_NINJA_USER), Collections.emptySet());
        sasuke = new Ninja(2L, "Sasuke Uchiha", "sasuke@gmail.com", "Sasuke12345.", Rank.GENIN, new Village(), 0, false, Set.of(Role.ROLE_NINJA_USER), Collections.emptySet());
        kage = new Ninja(3L, "Tsunade Senju", "tsunade@gmail.com", "Tsunade12345.", Rank.KAGE, new Village(), 0, false, Set.of(Role.ROLE_KAGE), Collections.emptySet());

        missionA = new Mission(1L, "Misión de limpieza", "Limpia la propiedad del señor feudal", 50, MissionDifficulty.D, Status.COMPLETED, LocalDateTime.now(), Set.of(naruto, sasuke));
        missionB = new Mission(2L, "Captura del Jinchuriki", "Capturar a Killer B", 5000, MissionDifficulty.A, Status.COMPLETED, LocalDateTime.now(), Set.of());

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
}
