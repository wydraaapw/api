package org.example.api.dto;

import jakarta.validation.constraints.*;

public record WaiterRequest(
    @NotBlank @Email String email,
    @NotBlank @Size(min = 6) String password,
    @NotBlank String firstName,
    @NotBlank String lastName,
    @NotBlank @Pattern(regexp = "\\d{9}") String phoneNumber,
    
    boolean speaksEnglish
) {}