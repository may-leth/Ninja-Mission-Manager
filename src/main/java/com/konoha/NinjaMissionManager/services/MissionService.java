package com.konoha.NinjaMissionManager.services;

import com.konoha.NinjaMissionManager.dtos.mission.MissionMapper;
import com.konoha.NinjaMissionManager.dtos.mission.MissionRequest;
import com.konoha.NinjaMissionManager.dtos.mission.MissionResponse;
import com.konoha.NinjaMissionManager.dtos.mission.MissionSummaryResponse;
import com.konoha.NinjaMissionManager.exceptions.ResourceConflictException;
import com.konoha.NinjaMissionManager.exceptions.ResourceNotFoundException;
import com.konoha.NinjaMissionManager.models.*;
import com.konoha.NinjaMissionManager.repositories.MissionRepository;
import com.konoha.NinjaMissionManager.specifications.MissionSpecificationBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

        if (isKage(authenticatedNinja) && !mission.getAssignedNinjas().contains(authenticatedNinja)) {
            throw new AccessDeniedException("You do not have permission to view this mission.");
        }

        return missionMapper.entityToDto(mission);
    }

    @Transactional
    public MissionResponse createMission(MissionRequest request, Principal principal){
        validateKagePermission(principal);
        validateMissionTitle(request.title());

        Set<Ninja> assignedNinjas = getAndValidateAssignedNinjas(request.ninjaId(), request.difficulty());

        Mission newMission = Mission.builder()
                .title(request.title())
                .description(request.description())
                .reward(request.reward())
                .difficulty(request.difficulty())
                .status(Status.PENDING)
                .creationDate(LocalDateTime.now())
                .assignedNinjas(assignedNinjas)
                .build();

        Mission savedMission = missionRepository.save(newMission);

        return missionMapper.entityToDto(savedMission);
    }

    private void validateKagePermission(Principal principal) {
        Ninja authenticatedNinja = ninjaService.getAuthenticatedNinja(principal);
        if (isKage(authenticatedNinja)) {
            throw new AccessDeniedException("Only a Kage can create or manage missions.");
        }
    }

    private void validateMissionTitle(String title) {
        if (missionRepository.existsByTitle(title)) {
            throw new ResourceConflictException("Mission with this title already exists.");
        }
    }

    private Set<Ninja> getAndValidateAssignedNinjas(Set<Long> ninjaIds, MissionDifficulty difficulty) {
        Set<Ninja> assignedNinjas = ninjaIds.stream()
                .map(ninjaService::getNinjaEntityById)
                .collect(Collectors.toSet());

        if (difficulty.isHighRank()) {
            boolean hasHighRankNinja = assignedNinjas.stream()
                    .anyMatch(ninja -> ninja.getRank().isAbove(Rank.CHUNIN));
            if (!hasHighRankNinja) {
                throw new AccessDeniedException("High-rank missions must be assigned to at least one Jonin or higher-rank ninja.");
            }
        }
        return assignedNinjas;
    }

    private boolean isKage(Ninja ninja) {
        return ninja.getRoles().stream()
                .noneMatch(role -> role.equals(Role.ROLE_KAGE));
    }

    private Mission findMissionById(Long id) {
        return missionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found with ID: " + id));
    }

}
