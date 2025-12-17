package org.example.api.dto;

import org.example.api.model.ReservationStatus;
import java.time.LocalDateTime;
import java.util.List;

public record ReservationResponse(
    Long id,
    Long clientId,
    String clientName,
    String clientEmail,
    Long tableId,
    int tableNumber,
    int seats,
    LocalDateTime start,
    LocalDateTime end,
    ReservationStatus status,
    Long waiterId,
    String waiterName
) {}