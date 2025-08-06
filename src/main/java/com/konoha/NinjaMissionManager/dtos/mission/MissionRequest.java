package com.konoha.NinjaMissionManager.dtos.mission;

import com.konoha.NinjaMissionManager.models.MissionDifficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record MissionRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 100, message = "Title cannot exceed 100 characters")
        String tile,

        @NotBlank(message = "Description is required")
        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description,

        @NotNull(message = "Reward is required")
        Integer reward,

        @NotNull(message = "Difficulty is required")
        MissionDifficulty difficulty,

        @NotNull(message = "Ninjas ID are required")
        Set<Long> ninjaId
) {
}