package org.example.api.unit.model;

import org.example.api.model.RestaurantTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantTableTest {

    @Nested
    @DisplayName("Builder and Constructor Tests")
    class BuilderTests {

        @Test
        @DisplayName("Should create RestaurantTable using builder")
        void shouldCreateRestaurantTableUsingBuilder() {
            RestaurantTable table = RestaurantTable.builder()
                    .id(1L)
                    .tableNumber(5)
                    .seats(4)
                    .rowPosition(2)
                    .columnPosition(3)
                    .build();

            assertEquals(1L, table.getId());
            assertEquals(5, table.getTableNumber());
            assertEquals(4, table.getSeats());
            assertEquals(2, table.getRowPosition());
            assertEquals(3, table.getColumnPosition());
        }

        @Test
        @DisplayName("Should create RestaurantTable using no-args constructor")
        void shouldCreateRestaurantTableUsingNoArgsConstructor() {
            RestaurantTable table = new RestaurantTable();
            assertNotNull(table);
            assertNull(table.getId());
        }
    }

    @Nested
    @DisplayName("Getters and Setters Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get all fields")
        void shouldSetAndGetAllFields() {
            RestaurantTable table = new RestaurantTable();
            
            table.setId(1L);
            table.setTableNumber(10);
            table.setSeats(6);
            table.setRowPosition(1);
            table.setColumnPosition(5);

            assertEquals(1L, table.getId());
            assertEquals(10, table.getTableNumber());
            assertEquals(6, table.getSeats());
            assertEquals(1, table.getRowPosition());
            assertEquals(5, table.getColumnPosition());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal when ids are the same")
        void shouldBeEqualWhenIdsAreSame() {
            RestaurantTable t1 = RestaurantTable.builder().id(1L).tableNumber(1).build();
            RestaurantTable t2 = RestaurantTable.builder().id(1L).tableNumber(2).build();

            assertEquals(t1, t2);
        }

        @Test
        @DisplayName("Should not be equal when ids are different")
        void shouldNotBeEqualWhenIdsAreDifferent() {
            RestaurantTable t1 = RestaurantTable.builder().id(1L).build();
            RestaurantTable t2 = RestaurantTable.builder().id(2L).build();

            assertNotEquals(t1, t2);
        }
    }
}