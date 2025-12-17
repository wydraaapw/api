package org.example.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.api.dto.WaiterRequest;
import org.example.api.dto.WaiterResponse;
import org.example.api.service.WaiterService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/admin/waiters")
public class WaiterController {

    private final WaiterService waiterService;
    @GetMapping
    public ResponseEntity<List<WaiterResponse>> getAll(){
        return ResponseEntity.ok(waiterService.findAll());
    }

    @PostMapping
    public ResponseEntity<WaiterResponse> create(@RequestBody @Valid WaiterRequest request) {
        return ResponseEntity.ok(waiterService.create(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        waiterService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
