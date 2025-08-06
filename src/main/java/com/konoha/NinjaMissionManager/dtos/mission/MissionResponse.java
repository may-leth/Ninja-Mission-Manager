package com.konoha.NinjaMissionManager.dtos.mission;

import com.konoha.NinjaMissionManager.dtos.ninja.NinjaSummaryResponse;
import com.konoha.NinjaMissionManager.models.MissionDifficulty;
import com.konoha.NinjaMissionManager.models.Status;

import java.time.LocalDateTime;
import java.util.Set;

public record MissionResponse(
        Long id,
        String title,
        String description,
        Integer reward,
        MissionDifficulty difficulty,
        Status status,
        LocalDateTime creationDate,
        Set<NinjaSummaryResponse> assignedNinjas
) {
}