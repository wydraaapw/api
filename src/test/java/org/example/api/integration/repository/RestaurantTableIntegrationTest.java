package org.example.api.integration.repository;

import org.example.api.model.RestaurantTable;
import org.example.api.repository.RestaurantTableRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RestaurantTable Integration Tests")
class RestaurantTableIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private RestaurantTableRepository tableRepository;

    @Test
    @DisplayName("Should save RestaurantTable with all fields")
    void shouldSaveRestaurantTable() {
        RestaurantTable table = RestaurantTable.builder()
                .tableNumber(1)
                .seats(4)
                .rowPosition(0)
                .columnPosition(0)
                .build();

        RestaurantTable saved = tableRepository.save(table);

        assertNotNull(saved.getId());
        assertEquals(1, saved.getTableNumber());
        assertEquals(4, saved.getSeats());
        assertEquals(0, saved.getRowPosition());
        assertEquals(0, saved.getColumnPosition());
    }

    @Test
    @DisplayName("Should enforce unique table number")
    void shouldEnforceUniqueTableNumber() {
        tableRepository.saveAndFlush(RestaurantTable.builder()
                .tableNumber(99)
                .seats(2)
                .rowPosition(1)
                .columnPosition(1)
                .build());

        RestaurantTable duplicate = RestaurantTable.builder()
                .tableNumber(99)
                .seats(4)
                .rowPosition(2)
                .columnPosition(2)
                .build();

        assertThrows(Exception.class, () -> tableRepository.saveAndFlush(duplicate));
    }

    @Test
    @DisplayName("Should update RestaurantTable")
    void shouldUpdateRestaurantTable() {
        RestaurantTable table = tableRepository.save(RestaurantTable.builder()
                .tableNumber(10)
                .seats(2)
                .rowPosition(0)
                .columnPosition(0)
                .build());

        table.setSeats(6);
        table.setRowPosition(5);
        tableRepository.save(table);

        RestaurantTable updated = tableRepository.findById(table.getId()).orElseThrow();
        assertEquals(6, updated.getSeats());
        assertEquals(5, updated.getRowPosition());
    }

    @Test
    @DisplayName("Should find all tables")
    void shouldFindAllTables() {
        tableRepository.save(RestaurantTable.builder()
                .tableNumber(1).seats(2).rowPosition(0).columnPosition(0).build());
        tableRepository.save(RestaurantTable.builder()
                .tableNumber(2).seats(4).rowPosition(0).columnPosition(1).build());
        tableRepository.save(RestaurantTable.builder()
                .tableNumber(3).seats(6).rowPosition(1).columnPosition(0).build());

        List<RestaurantTable> tables = tableRepository.findAll();

        assertEquals(3, tables.size());
    }

    @Test
    @DisplayName("Should delete RestaurantTable")
    void shouldDeleteRestaurantTable() {
        RestaurantTable table = tableRepository.save(RestaurantTable.builder()
                .tableNumber(100)
                .seats(2)
                .rowPosition(0)
                .columnPosition(0)
                .build());

        tableRepository.deleteById(table.getId());

        assertFalse(tableRepository.existsById(table.getId()));
    }
}