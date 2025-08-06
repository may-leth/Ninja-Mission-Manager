package com.konoha.NinjaMissionManager.dtos.ninja;

import com.konoha.NinjaMissionManager.dtos.mission.MissionSummaryResponse;
import com.konoha.NinjaMissionManager.dtos.village.VillageResponse;

import java.util.Set;

public record NinjaResponse(
        Long id,
        String name,
        String email,
        String rank,
        VillageResponse village,
        Integer missionCompletedCount,
        Boolean isAnbu,
        Set<MissionSummaryResponse> assignedMissions
) {
}