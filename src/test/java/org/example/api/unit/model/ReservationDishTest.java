package org.example.api.unit.model;

import org.example.api.model.Dish;
import org.example.api.model.Reservation;
import org.example.api.model.ReservationDish;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReservationDishTest {

    @Nested
    @DisplayName("Builder and Constructor Tests")
    class BuilderTests {

        @Test
        @DisplayName("Should create ReservationDish using builder")
        void shouldCreateReservationDishUsingBuilder() {
            Reservation reservation = Reservation.builder().id(1L).build();
            Dish dish = Dish.builder().id(1L).name("Pizza").build();

            ReservationDish reservationDish = ReservationDish.builder()
                    .id(1L)
                    .quantity(3)
                    .isServed(true)
                    .reservation(reservation)
                    .dish(dish)
                    .build();

            assertEquals(1L, reservationDish.getId());
            assertEquals(3, reservationDish.getQuantity());
            assertTrue(reservationDish.isServed());
            assertEquals(reservation, reservationDish.getReservation());
            assertEquals(dish, reservationDish.getDish());
        }

        @Test
        @DisplayName("Should have isServed default to false")
        void shouldHaveIsServedDefaultToFalse() {
            ReservationDish reservationDish = ReservationDish.builder()
                    .quantity(1)
                    .build();

            assertFalse(reservationDish.isServed());
        }
    }

    @Nested
    @DisplayName("Getters and Setters Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get quantity")
        void shouldSetAndGetQuantity() {
            ReservationDish reservationDish = new ReservationDish();
            reservationDish.setQuantity(5);
            assertEquals(5, reservationDish.getQuantity());
        }

        @Test
        @DisplayName("Should set and get isServed")
        void shouldSetAndGetIsServed() {
            ReservationDish reservationDish = new ReservationDish();
            reservationDish.setServed(true);
            assertTrue(reservationDish.isServed());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal when ids are the same")
        void shouldBeEqualWhenIdsAreSame() {
            ReservationDish rd1 = ReservationDish.builder().id(1L).quantity(1).build();
            ReservationDish rd2 = ReservationDish.builder().id(1L).quantity(5).build();

            assertEquals(rd1, rd2);
        }

        @Test
        @DisplayName("Should not be equal when ids are different")
        void shouldNotBeEqualWhenIdsAreDifferent() {
            ReservationDish rd1 = ReservationDish.builder().id(1L).build();
            ReservationDish rd2 = ReservationDish.builder().id(2L).build();

            assertNotEquals(rd1, rd2);
        }
    }
}