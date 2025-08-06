package com.konoha.NinjaMissionManager.dtos.ninja;

import com.konoha.NinjaMissionManager.dtos.village.VillageResponse;

public record NinjaResponse(
        Long id,
        String name,
        String email,
        String rank,
        VillageResponse village,
        Integer missionCompletedCount,
        Boolean isAnbu
) {
}