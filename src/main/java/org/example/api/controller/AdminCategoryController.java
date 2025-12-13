package org.example.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.api.dto.CategoryRequest;
import org.example.api.model.Category;
import org.example.api.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminCategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<Category>> getAll() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @PostMapping
    public ResponseEntity<Category> create(@RequestBody @Valid CategoryRequest request) {
        return ResponseEntity.ok(categoryService.create(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
