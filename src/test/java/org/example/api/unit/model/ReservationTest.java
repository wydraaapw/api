package org.example.api.unit.model;

import io.hypersistence.utils.hibernate.type.range.Range;
import org.example.api.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;


import java.time.LocalDateTime;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class ReservationTest {

    @Nested
    @DisplayName("Builder and Constructor Tests")
    class BuilderTests {

        @Test
        @DisplayName("Should create Reservation using builder")
        void shouldCreateReservationUsingBuilder() {
            LocalDateTime start = LocalDateTime.of(2024, 1, 15, 18, 0);
            LocalDateTime end = LocalDateTime.of(2024, 1, 15, 20, 0);
            Range<LocalDateTime> period = Range.closedOpen(start, end);
            
            Client client = Client.builder().id(1L).build();
            RestaurantTable table = RestaurantTable.builder().id(1L).tableNumber(5).build();

            Reservation reservation = Reservation.builder()
                    .id(1L)
                    .reservationPeriod(period)
                    .status(ReservationStatus.CONFIRMED)
                    .restaurantTable(table)
                    .client(client)
                    .build();

            assertEquals(1L, reservation.getId());
            assertEquals(period, reservation.getReservationPeriod());
            assertEquals(ReservationStatus.CONFIRMED, reservation.getStatus());
            assertEquals(table, reservation.getRestaurantTable());
            assertEquals(client, reservation.getClient());
        }

        @Test
        @DisplayName("Should initialize reservationDishes as empty HashSet by default")
        void shouldInitializeReservationDishesAsEmptyHashSet() {
            Reservation reservation = Reservation.builder().build();

            assertNotNull(reservation.getReservationDishes());
            assertTrue(reservation.getReservationDishes().isEmpty());
            assertInstanceOf(HashSet.class, reservation.getReservationDishes());
        }

        @Test
        @DisplayName("Should allow null waiter (optional)")
        void shouldAllowNullWaiter() {
            Reservation reservation = Reservation.builder()
                    .id(1L)
                    .waiter(null)
                    .build();

            assertNull(reservation.getWaiter());
        }
    }

    @Nested
    @DisplayName("Reservation Status Tests")
    class StatusTests {

        @Test
        @DisplayName("Should set PENDING status")
        void shouldSetPendingStatus() {
            Reservation reservation = Reservation.builder()
                    .status(ReservationStatus.PENDING)
                    .build();
            assertEquals(ReservationStatus.PENDING, reservation.getStatus());
        }

        @Test
        @DisplayName("Should set CONFIRMED status")
        void shouldSetConfirmedStatus() {
            Reservation reservation = Reservation.builder()
                    .status(ReservationStatus.CONFIRMED)
                    .build();
            assertEquals(ReservationStatus.CONFIRMED, reservation.getStatus());
        }

        @Test
        @DisplayName("Should set IN_PROGRESS status")
        void shouldSetInProgressStatus() {
            Reservation reservation = Reservation.builder()
                    .status(ReservationStatus.IN_PROGRESS)
                    .build();
            assertEquals(ReservationStatus.IN_PROGRESS, reservation.getStatus());
        }

        @Test
        @DisplayName("Should set COMPLETED status")
        void shouldSetCompletedStatus() {
            Reservation reservation = Reservation.builder()
                    .status(ReservationStatus.COMPLETED)
                    .build();
            assertEquals(ReservationStatus.COMPLETED, reservation.getStatus());
        }

        @Test
        @DisplayName("Should set CANCELLED status")
        void shouldSetCancelledStatus() {
            Reservation reservation = Reservation.builder()
                    .status(ReservationStatus.CANCELLED)
                    .build();
            assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());
        }
    }

    @Nested
    @DisplayName("Reservation Dishes Collection Tests")
    class ReservationDishesTests {

        @Test
        @DisplayName("Should add ReservationDish to reservation")
        void shouldAddReservationDishToReservation() {
            Reservation reservation = Reservation.builder().id(1L).build();
            Dish dish = Dish.builder().id(1L).name("Pizza").build();
            
            ReservationDish reservationDish = ReservationDish.builder()
                    .id(1L)
                    .reservation(reservation)
                    .dish(dish)
                    .quantity(2)
                    .build();

            reservation.getReservationDishes().add(reservationDish);

            assertEquals(1, reservation.getReservationDishes().size());
            assertTrue(reservation.getReservationDishes().contains(reservationDish));
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal when ids are the same")
        void shouldBeEqualWhenIdsAreSame() {
            Reservation r1 = Reservation.builder().id(1L).status(ReservationStatus.PENDING).build();
            Reservation r2 = Reservation.builder().id(1L).status(ReservationStatus.CONFIRMED).build();

            assertEquals(r1, r2);
        }

        @Test
        @DisplayName("Should not be equal when ids are different")
        void shouldNotBeEqualWhenIdsAreDifferent() {
            Reservation r1 = Reservation.builder().id(1L).build();
            Reservation r2 = Reservation.builder().id(2L).build();

            assertNotEquals(r1, r2);
        }
    }
}