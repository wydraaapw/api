package org.example.api.dto;

import java.math.BigDecimal;
import java.util.List;

public record DishResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String imageUrl,
        String categoryName,
        List<String> ingredients
) {}