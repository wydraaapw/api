package org.example.api.controller;

import lombok.RequiredArgsConstructor;
import org.example.api.dto.DishResponse;
import org.example.api.service.DishService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menu")
public class ClientMenuController {
    private final DishService dishService;

    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENT', 'WAITER', 'ADMIN')")
    public ResponseEntity<List<DishResponse>> getMenu() {
        return ResponseEntity.ok(dishService.findAll());
    }
}
