package org.example.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.api.dto.ReservationRequest;
import org.example.api.service.ReservationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/occupied")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN', 'WAITER')")
    public ResponseEntity<List<Long>> getOccupiedTables(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return ResponseEntity.ok(reservationService.getOccupiedTableIds(start, end));
    }

    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Void> createReservation(
            @RequestBody @Valid ReservationRequest request,
            Authentication authentication
    ) {
        reservationService.createReservation(authentication.getName(), request);
        return ResponseEntity.ok().build();
    }
}