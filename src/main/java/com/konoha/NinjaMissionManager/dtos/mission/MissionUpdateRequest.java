package com.konoha.NinjaMissionManager.dtos.mission;

import com.konoha.NinjaMissionManager.models.MissionDifficulty;
import com.konoha.NinjaMissionManager.models.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record MissionUpdateRequest(

        @Size(max = 100, message = "Title cannot exceed 100 characters")
        String title,

        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description,

        @PositiveOrZero(message = "Reward must be a positive number or zero")
        Integer reward,

        MissionDifficulty difficulty,

        @NotNull(message = "Status cannot be empty")
        Status status,

        Set<Long> ninjaId
) {
}