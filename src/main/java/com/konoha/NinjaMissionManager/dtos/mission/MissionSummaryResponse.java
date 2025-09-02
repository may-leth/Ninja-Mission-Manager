package com.konoha.NinjaMissionManager.dtos.mission;

import com.konoha.NinjaMissionManager.models.MissionDifficulty;
import com.konoha.NinjaMissionManager.models.Status;

public record MissionSummaryResponse(
        Long id,
        String title,
        MissionDifficulty difficulty,
        Status status
) {}