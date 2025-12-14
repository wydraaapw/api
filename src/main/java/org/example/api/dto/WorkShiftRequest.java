package org.example.api.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record WorkShiftRequest(
        @NotNull Long waiterId,
        @NotNull LocalDateTime start,
        @NotNull LocalDateTime end
) {}
