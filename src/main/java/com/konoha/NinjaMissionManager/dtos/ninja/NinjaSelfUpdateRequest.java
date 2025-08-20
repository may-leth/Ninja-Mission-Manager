package com.konoha.NinjaMissionManager.dtos.ninja;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record NinjaSelfUpdateRequest(
        @NotBlank(message = "Name cannot be blank")
        String name,

        @Email(message = "Email not valid", regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
        String email,

        @Pattern(message = "Password must contain a minimum of 8 characters, including a number, one uppercase letter, one lowercase letter and one special character", regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=.])(?=\\S+$).{8,}$")
        String password
) {
}
