package com.konoha.NinjaMissionManager.dtos.village;

public record VillageUpdateRequest(
        String name,
        Long kageId
) {
}