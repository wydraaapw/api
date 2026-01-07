package org.example.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.api.dto.ReservationRequest;
import org.example.api.dto.ReservationResponse;
import org.example.api.dto.WaiterResponse;
import org.example.api.model.ReservationStatus;
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

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAITER')")
    public ResponseEntity<List<ReservationResponse>> getAllReservations() {
        return ResponseEntity.ok(reservationService.findAll());
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAITER', 'CLIENT')")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam ReservationStatus status
    ) {
        reservationService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/available-waiters")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<WaiterResponse>> getAvailableWaiters(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getAvailableWaitersForReservation(id));
    }

    @PatchMapping("/{id}/assign-waiter")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> assignWaiter(
            @PathVariable Long id,
            @RequestParam Long waiterId
    ) {
        reservationService.assignWaiter(id, waiterId);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/my")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<ReservationResponse>> getClientReservations(Authentication authentication) {
        return ResponseEntity.ok(reservationService.findClientReservations(authentication.getName()));
    }

    @GetMapping("/waiter-my")
    @PreAuthorize("hasRole('WAITER')")
    public ResponseEntity<List<ReservationResponse>> getWaiterReservations(Authentication authentication){
        return ResponseEntity.ok(reservationService.findWaiterReservations(authentication.getName()));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long id, Authentication authentication){
        reservationService.cancelReservation(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/dish/{dishId}/status")
    @PreAuthorize("hasRole('WAITER')")
    public ResponseEntity<Void> updateDishStatus(@PathVariable Long id, @PathVariable Long dishId, Authentication authentication){
        reservationService.updateReservationDishStatus(id, dishId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}