package org.example.api.dto;

public record ReservationDishResponse(
        Long id,
        String name,
        Integer quantity,
        boolean isServed
)
{ }
