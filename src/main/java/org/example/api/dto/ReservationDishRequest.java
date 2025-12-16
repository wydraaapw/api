package org.example.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReservationDishRequest(
    @NotNull Long dishId,
    @Min(1) Integer quantity
) {}