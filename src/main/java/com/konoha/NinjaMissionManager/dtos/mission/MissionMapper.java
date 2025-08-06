package com.konoha.NinjaMissionManager.dtos.mission;

import com.konoha.NinjaMissionManager.models.Mission;
import com.konoha.NinjaMissionManager.models.Ninja;
import com.konoha.NinjaMissionManager.models.Status;

import java.time.LocalDateTime;
import java.util.Set;

public interface MissionMapper {
    Mission dtoToEntity(MissionRequest dto, Status status, LocalDateTime creationDate, Set<Ninja> assignedNinjas);
    MissionResponse entityToDto(Mission mission);
    MissionSummaryResponse entityToSummaryDto(Mission mission);
}