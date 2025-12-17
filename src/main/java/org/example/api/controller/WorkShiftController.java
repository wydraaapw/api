package org.example.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.api.dto.WorkShiftRequest;
import org.example.api.dto.WorkShiftResponse;
import org.example.api.service.WorkShiftService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/admin/shifts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class WorkShiftController {

    private final WorkShiftService workShiftService;

    @GetMapping
    public ResponseEntity<Page<WorkShiftResponse>> getAll(
            @RequestParam(required = false) Long waiterId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(workShiftService.getAll(waiterId, page, size));
    }

    @PostMapping
    public ResponseEntity<WorkShiftResponse> create(@RequestBody @Valid WorkShiftRequest request) {
        return ResponseEntity.ok(workShiftService.create(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        workShiftService.delete(id);
        return ResponseEntity.noContent().build();
    }
}