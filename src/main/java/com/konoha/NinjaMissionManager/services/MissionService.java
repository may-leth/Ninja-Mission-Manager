package com.konoha.NinjaMissionManager.services;

import com.konoha.NinjaMissionManager.dtos.mission.*;
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
import java.util.ArrayList;
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
    public MissionResponse createMission(MissionCreateRequest request, Principal principal){
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

    @Transactional
    public MissionResponse updateMission(Long id, MissionUpdateRequest request, Principal principal){
        Ninja authenicatedNinja = ninjaService.getAuthenticatedNinja(principal);
        Mission mission = findMissionById(id);
        boolean isKage = isKage(authenicatedNinja);

        if (!isKage){
            if (!mission.getAssignedNinjas().contains(authenicatedNinja)){
                throw new AccessDeniedException("You do not have permission to update this mission.");
            }
            if (request.status() == null || request.title() != null || request.description() != null || request.reward() != null || request.difficulty() != null || request.ninjaId() != null){
                throw new AccessDeniedException("Only the mission status can be updated by a ninja.");
            }
            if (request.status() != mission.getStatus()) {
                if (request.status() == Status.COMPLETED){
                    updateCompletedMissionCount(mission);
                }
                mission.setStatus(request.status());
            }
            Mission updatedMission = missionRepository.save(mission);
            return missionMapper.entityToDto(updatedMission);
        }

        if (!mission.getTitle().equals(request.title()) && missionRepository.existsByTitle(request.title())) {
            throw new ResourceConflictException("Mission with this title already exists.");
        }

        Set<Ninja> assignedNinjas = getAndValidateAssignedNinjas(request.ninjaId(), request.difficulty());

        if (request.status() != null && request.status() != mission.getStatus()) {
            if (request.status() == Status.COMPLETED) {
                updateCompletedMissionCount(mission);
            }
            mission.setStatus(request.status());
        }

        mission.setTitle(request.title());
        mission.setDescription(request.description());
        mission.setReward(request.reward());
        mission.setDifficulty(request.difficulty());
        mission.setAssignedNinjas(assignedNinjas);

        Mission updatedMission = missionRepository.save(mission);
        return missionMapper.entityToDto(updatedMission);
    }

    private void updateCompletedMissionCount(Mission mission) {
        List<Ninja> ninjasToUpdate = new ArrayList<>(mission.getAssignedNinjas());

        for (Ninja ninja : ninjasToUpdate) {
            ninja.setMissionsCompletedCount(ninja.getMissionsCompletedCount() + 1);
        }

        ninjaService.saveAllNinjas(ninjasToUpdate);
    }

    private void validateKagePermission(Principal principal) {
        Ninja authenticatedNinja = ninjaService.getAuthenticatedNinja(principal);
        if (!isKage(authenticatedNinja)) {
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
                .anyMatch(role -> role == Role.ROLE_KAGE);
    }

    private Mission findMissionById(Long id) {
        return missionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found with ID: " + id));
    }
}
