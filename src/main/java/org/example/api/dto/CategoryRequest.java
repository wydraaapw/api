package org.example.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
        @NotBlank(message = "Nazwa kategorii jest wymagana")
        String name
) {}
