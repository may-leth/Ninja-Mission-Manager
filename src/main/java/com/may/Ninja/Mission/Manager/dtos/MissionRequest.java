package com.may.Ninja.Mission.Manager.dtos;

import com.may.Ninja.Mission.Manager.models.Rank;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MissionRequest(
        @NotBlank(message = "Please provide a mission name")
        String name,

        @NotNull(message = "Rank is mandatory")
        Rank rank,

        @NotNull(message = "A ninja must be assigned")
        String assignedTo,

        Boolean completed
) {
}
