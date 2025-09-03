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
import java.util.*;
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
                .status(status);

        Optional<Long> ninjaIdToFilter = isKage(authenticatedNinja) ? assignToNinjaId : Optional.of(authenticatedNinja.getId());
        builder.assignedToNinja(ninjaIdToFilter);

        Specification<Mission> finalSpecification = builder.build();
        List<Mission> missions = missionRepository.findAll(finalSpecification);

        return missions.stream()
                .map(missionMapper::entityToSummaryDto)
                .toList();
    }

    public MissionResponse getMissionById(Long requestedId, Principal principal) {
        Ninja authenticatedNinja = ninjaService.getAuthenticatedNinja(principal);
        Mission mission = findMissionById(requestedId);

        if (!isKage(authenticatedNinja) && !mission.getAssignedNinjas().contains(authenticatedNinja)) {
            throw new AccessDeniedException("You do not have permission to view this mission.");
        }

        return missionMapper.entityToDto(mission);
    }

    @Transactional
    public MissionResponse createMission(MissionCreateRequest request, Principal principal){
        validateKagePermission(principal);
        validateMissionTitle(request.title());

        Mission newMission = missionMapper.dtoToEntity(request);

        Set<Ninja> assignedNinjas = getAndValidateAssignedNinjas(request.ninjaId(), request.difficulty());

        newMission.setStatus(Status.PENDING);
        newMission.setAssignedNinjas(assignedNinjas);
        newMission.setCreationDate(LocalDateTime.now());

        Mission savedMission = missionRepository.save(newMission);

        return missionMapper.entityToDto(savedMission);
    }

    @Transactional
    public MissionResponse updateMission(Long id, MissionUpdateRequest request, Principal principal){
        Ninja authenticatedNinja = ninjaService.getAuthenticatedNinja(principal);
        Mission mission = findMissionById(id);

        if (!isKage(authenticatedNinja)){
            return updateMissionAsNinja(mission, authenticatedNinja, request);
        }

        return updateMissionAsKage(mission, request);
    }

    @Transactional
    public void deleteMission(Long id, Principal principal){
        validateKagePermission(principal);

        Mission missionToDelete = findMissionById(id);

        missionRepository.delete(missionToDelete);
    }

    private MissionResponse updateMissionAsNinja(Mission mission, Ninja authenticatedNinja, MissionUpdateRequest request){
        if (!mission.getAssignedNinjas().contains(authenticatedNinja)){
            throw new AccessDeniedException("You do not have permission to update this mission.");
        }

        boolean anyOtherFieldIsPresent = request.title() != null ||
                request.description() != null ||
                request.reward() != null ||
                request.difficulty() != null ||
                request.ninjaIds() != null;

        if (anyOtherFieldIsPresent) {
            throw new AccessDeniedException("Only the mission status can be updated by a assigned ninja.");
        }

        updateStatusIfChanged(mission, request.status());

        Mission updatedMission = missionRepository.save(mission);
        return missionMapper.entityToDto(updatedMission);
    }

    private MissionResponse updateMissionAsKage(Mission mission,MissionUpdateRequest request){
        if (request.title() != null && !mission.getTitle().equals(request.title())){
            validateMissionTitle(request.title());
        }

        missionMapper.updateEntityFromDto(request, mission);

        if (request.ninjaIds() != null){
            Set<Ninja> newAssignedNinjas = getAndValidateAssignedNinjas(request.ninjaIds(), mission.getDifficulty());
            mission.setAssignedNinjas(newAssignedNinjas);
        }

        updateStatusIfChanged(mission, request.status());

        Mission updatedMission = missionRepository.save(mission);
        return missionMapper.entityToDto(updatedMission);
    }

    private void updateStatusIfChanged(Mission mission, Status newStatus){
        if (newStatus != null && newStatus != mission.getStatus()){
            if (newStatus == Status.COMPLETED){
                updateCompletedMissionCount(mission);
            }
            mission.setStatus(newStatus);
        }
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