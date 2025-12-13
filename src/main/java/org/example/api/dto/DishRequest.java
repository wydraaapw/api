package org.example.api.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public record DishRequest(
        @NotBlank(message = "Nazwa jest wymagana")
        String name,

        String description,

        @NotNull(message = "Cena jest wymagana")
        @Positive(message = "Cena musi być dodatnia")
        BigDecimal price,

        @NotBlank(message = "Zdjęcie jest wymagane")
        String imageUrl,

        @NotNull(message = "Kategoria jest wymagana")
        Long categoryId,

        List<Long> ingredientIds
) {}