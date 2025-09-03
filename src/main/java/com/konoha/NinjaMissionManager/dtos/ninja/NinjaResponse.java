package com.konoha.NinjaMissionManager.dtos.ninja;

import com.konoha.NinjaMissionManager.dtos.mission.MissionSummaryResponse;
import java.util.Set;

public record NinjaResponse(
        Long id,
        String name,
        String email,
        String rank,
        String village,
        Integer missionsCompletedCount,
        Boolean isAnbu,
        Set<MissionSummaryResponse> assignedMissions
) {}