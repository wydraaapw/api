package org.example.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank @Email String email, @NotBlank @Size(min = 6) String password,
    @NotBlank String firstName, @NotBlank String lastName,
    @NotBlank @Pattern(regexp = "\\d{9}") String phoneNumber
) {}