package org.example.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.api.dto.RestaurantTableRequest;
import org.example.api.dto.RestaurantTableResponse;
import org.example.api.model.RestaurantTable;
import org.example.api.service.RestaurantTableService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/tables")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class TableController {

    private final RestaurantTableService tableService;

    @GetMapping
    public ResponseEntity<List<RestaurantTableResponse>> getAll() {
        return ResponseEntity.ok(tableService.findAll());
    }

    @PostMapping
    public ResponseEntity<RestaurantTable> create(@RequestBody @Valid RestaurantTableRequest request) {
        RestaurantTable table = RestaurantTable.builder()
                .tableNumber(request.tableNumber())
                .seats(request.seats())
                .rowPosition(request.rowPosition())
                .columnPosition(request.columnPosition())
                .tableType(request.tableType())
                .build();
        return ResponseEntity.ok(tableService.create(table));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tableService.delete(id);
        return ResponseEntity.noContent().build();
    }
}