package org.controllers.DTO.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest (
        @NotBlank(message = "Login must be non-empty")
        @Size(max = 60, message = "Login must not exceed 60 characters")
        String login,
        @NotBlank(message = "Password must be non-empty")
        @Size(min = 8, max = 72, message = "Password must contain from 8 to 72 characters")
        String password,
        @NotBlank(message = "Invite code must be non-empty")
        String inviteCode
) {}
