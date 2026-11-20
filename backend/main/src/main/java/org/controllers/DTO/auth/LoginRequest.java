package org.controllers.DTO.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "Login must be non-empty")
        @Size(max = 60, message = "Login must not exceed 60 characters")
        String login,
        @NotBlank(message = "Password must be non-empty")
        @Size(max = 72, message = "Password must not exceed 72 characters")
        String password
) {}
