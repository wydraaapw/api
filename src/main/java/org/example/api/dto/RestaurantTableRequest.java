package org.example.api.dto;

import jakarta.validation.constraints.NotNull;
import org.example.api.model.TableType;

public record RestaurantTableRequest(
            Integer tableNumber,
            Integer seats,
            @NotNull Integer rowPosition,
            @NotNull Integer columnPosition,
            TableType tableType
) {}