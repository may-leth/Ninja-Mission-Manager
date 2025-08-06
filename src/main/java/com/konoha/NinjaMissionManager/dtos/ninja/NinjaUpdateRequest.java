package com.konoha.NinjaMissionManager.dtos.ninja;

import com.konoha.NinjaMissionManager.models.Rank;
import com.konoha.NinjaMissionManager.models.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record NinjaUpdateRequest(
        @NotNull(message = "ID is required for update")
        Long id,

        String name,

        @Email(message = "Email not valid", regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
        String email,

        Rank rank,

        Long villageId,

        Boolean isAnbu,

        Set<Role> roles
) {
}