package com.github.discovery126.authservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RegisterRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Email is invalid")
        @Size(max = 128, message = "Email must be at most 128 characters")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\\$%^&*])(?=\\S+$).{8,}$",
                message = "Password must contain at least 1 uppercase letter, 1 digit, and 1 special character")
        String password
) {}