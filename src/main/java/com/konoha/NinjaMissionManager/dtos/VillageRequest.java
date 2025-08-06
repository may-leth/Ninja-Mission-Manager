package com.konoha.NinjaMissionManager.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VillageRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotNull(message = "Kage ID is required")
        Long kageId
) {
}