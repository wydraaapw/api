package org.example.api.dto;

import jakarta.validation.constraints.NotBlank;

public record IngredientRequest(
    @NotBlank(message = "Nazwa składnika jest wymagana")
    String name
) {}