package org.example.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.api.dto.DishRequest;
import org.example.api.dto.DishResponse;
import org.example.api.service.DishService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/dishes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDishController {
    private final DishService dishService;

    @GetMapping
    public ResponseEntity<List<DishResponse>> getAll() {
        return ResponseEntity.ok(dishService.findAll());
    }

    @PostMapping
    public ResponseEntity<DishResponse> create(@RequestBody @Valid DishRequest request) {
        return ResponseEntity.ok(dishService.create(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        dishService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
