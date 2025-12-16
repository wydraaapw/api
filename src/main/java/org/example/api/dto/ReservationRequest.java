package org.example.api.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public record ReservationRequest(
        @NotNull Long tableId,
        @NotNull LocalDateTime start,
        @NotNull LocalDateTime end,
        List<ReservationDishRequest> dishes
) {}