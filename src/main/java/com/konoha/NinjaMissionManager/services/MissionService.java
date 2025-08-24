package com.konoha.NinjaMissionManager.services;

import com.konoha.NinjaMissionManager.dtos.mission.MissionMapper;
import com.konoha.NinjaMissionManager.dtos.mission.MissionResponse;
import com.konoha.NinjaMissionManager.dtos.mission.MissionSummaryResponse;
import com.konoha.NinjaMissionManager.exceptions.ResourceNotFoundException;
import com.konoha.NinjaMissionManager.models.*;
import com.konoha.NinjaMissionManager.repositories.MissionRepository;
import com.konoha.NinjaMissionManager.specifications.MissionSpecificationBuilder;
import lombok.RequiredArgsConstructor;
import org.hibernate.event.spi.PreInsertEvent;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MissionService {
    private final MissionRepository missionRepository;
    private final MissionMapper missionMapper;
    private final NinjaService ninjaService;

    public List<MissionSummaryResponse> getAllMissions(Optional<MissionDifficulty> difficulty, Optional<Status> status, Optional<Long> assignToNinjaId, Principal principal){
        Ninja authenticatedNinja = ninjaService.getAuthenticatedNinja(principal);

        MissionSpecificationBuilder builder = MissionSpecificationBuilder.builder()
                .difficulty(difficulty)
                .status(status)
                .assignedToNinja(assignToNinjaId);

        boolean isKage = authenticatedNinja.getRoles().stream()
                .anyMatch(role -> role.equals(Role.ROLE_KAGE));

        if (!isKage) {
            builder.assignedToNinja(Optional.of(authenticatedNinja.getId()));
        }

        Specification<Mission> finalSpecification = builder.build();

        List<Mission> missions = missionRepository.findAll(finalSpecification);

        return missions.stream()
                .map(missionMapper::entityToSummaryDto)
                .toList();
    }

    public MissionResponse getMissionById(Long requestedId, Principal principal) {
        Ninja authenticatedNinja = ninjaService.getAuthenticatedNinja(principal);
        Mission mission = findMissionById(requestedId);

        boolean isKage = authenticatedNinja.getRoles().stream()
                .anyMatch(role -> role.equals(Role.ROLE_KAGE));

        if (isKage) {
            return missionMapper.entityToDto(mission);
        }

        boolean isAssigned = mission.getAssignedNinjas().contains(authenticatedNinja);
        if (!isAssigned) {
            throw new AccessDeniedException("You do not have permission to view this mission.");
        }

        return missionMapper.entityToDto(mission);
    }

    private Mission findMissionById(Long id) {
        return missionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found with ID: " + id));
    }

}
