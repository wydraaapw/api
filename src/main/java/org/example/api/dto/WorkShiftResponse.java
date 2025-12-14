package org.example.api.dto;


import java.time.LocalDateTime;

public record WorkShiftResponse(
        Long id,
        Long waiterId,
        String waiterName,
        LocalDateTime start,
        LocalDateTime end
) {}
