package org.example.api.dto;

import org.example.api.model.TableType;

public record RestaurantTableResponse(
        Long id,
        Integer tableNumber,
        Integer seats,
        Integer rowPosition,
        Integer columnPosition,
        TableType tableType
) {
}
