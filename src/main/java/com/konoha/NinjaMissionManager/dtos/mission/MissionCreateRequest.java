package com.konoha.NinjaMissionManager.dtos.mission;

import com.konoha.NinjaMissionManager.models.MissionDifficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record MissionCreateRequest(
        @NotBlank(message = "Title cannot be empty")
        @Size(max = 100, message = "Title cannot exceed 100 characters")
        String title,

        @NotBlank(message = "Description cannot be empty")
        String description,

        @NotNull(message = "Reward cannot be empty")
        @PositiveOrZero(message = "Reward must be a positive number or zero")
        Integer reward,

        @NotNull(message = "Difficulty cannot be empty")
        MissionDifficulty difficulty,

        @NotNull(message = "Ninjas must be assigned to the mission")
        Set<Long> ninjaId
) {
}
