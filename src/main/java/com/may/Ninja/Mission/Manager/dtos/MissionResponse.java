package com.may.Ninja.Mission.Manager.dtos;

import com.may.Ninja.Mission.Manager.models.Rank;

public record MissionResponse(
        Long id,
        String name,
        Rank rank,
        String assignedTo,
        Boolean completed
) {
}
