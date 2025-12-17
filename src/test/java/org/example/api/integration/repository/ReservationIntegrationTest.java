package org.example.api.integration.repository;

import io.hypersistence.utils.hibernate.type.range.Range;
import org.example.api.model.*;
import org.example.api.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Reservation Integration Tests")
class ReservationIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private WaiterRepository waiterRepository;
    @Autowired
    private RestaurantTableRepository tableRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private DishRepository dishRepository;

    private Client client;
    private Waiter waiter;
    private RestaurantTable table;

    @BeforeEach
    void setUp() {
        User clientUser = userRepository.save(User.builder()
                .email("res.client" + System.nanoTime() + "@test.pl")
                .passwordHash("hash")
                .firstName("Klient")
                .lastName("Rezerwacji")
                .phoneNumber("123456789")
                .role(Role.ROLE_CLIENT)
                .build());

        client = clientRepository.save(Client.builder().user(clientUser).build());

        User waiterUser = userRepository.save(User.builder()
                .email("res.waiter" + System.nanoTime() + "@test.pl")
                .passwordHash("hash")
                .firstName("Kelner")
                .lastName("Rezerwacji")
                .phoneNumber("987654321")
                .role(Role.ROLE_WAITER)
                .build());

        waiter = waiterRepository.save(Waiter.builder()
                .user(waiterUser)
                .hireDate(LocalDate.now())
                .speaksEnglish(true)
                .build());

        table = tableRepository.save(RestaurantTable.builder()
                .tableNumber((int) System.nanoTime() % 10000)
                .seats(4)
                .rowPosition(1)
                .columnPosition(1)
                .build());
    }

    @Nested
    @DisplayName("Basic Reservation Tests")
    class BasicTests {

        @Test
        @DisplayName("Should save Reservation with tsrange")
        void shouldSaveReservationWithTsrange() {
            LocalDateTime start = LocalDateTime.of(2024, 12, 15, 18, 0);
            LocalDateTime end = LocalDateTime.of(2024, 12, 15, 20, 0);

            Reservation reservation = Reservation.builder()
                    .reservationPeriod(Range.closedOpen(start, end))
                    .status(ReservationStatus.CONFIRMED)
                    .restaurantTable(table)
                    .client(client)
                    .waiter(waiter)
                    .build();

            Reservation saved = reservationRepository.save(reservation);

            assertNotNull(saved.getId());
            assertEquals(ReservationStatus.CONFIRMED, saved.getStatus());
            assertEquals(start, saved.getReservationPeriod().lower());
            assertEquals(end, saved.getReservationPeriod().upper());
        }

        @Test
        @DisplayName("Should allow null waiter (optional)")
        void shouldAllowNullWaiter() {
            LocalDateTime start = LocalDateTime.now().plusDays(1);

            Reservation reservation = Reservation.builder()
                    .reservationPeriod(Range.closedOpen(start, start.plusHours(2)))
                    .status(ReservationStatus.PENDING)
                    .restaurantTable(table)
                    .client(client)
                    .waiter(null)
                    .build();

            Reservation saved = reservationRepository.save(reservation);

            assertNull(saved.getWaiter());
        }

        @Test
        @DisplayName("Should test all ReservationStatus values")
        void shouldTestAllReservationStatusValues() {
            for (ReservationStatus status : ReservationStatus.values()) {
                RestaurantTable statusTable = tableRepository.save(RestaurantTable.builder()
                        .tableNumber((int) (System.nanoTime() % 10000))
                        .seats(2)
                        .rowPosition(0)
                        .columnPosition(status.ordinal())
                        .build());

                LocalDateTime start = LocalDateTime.now().plusDays(status.ordinal() + 1);

                Reservation reservation = Reservation.builder()
                        .reservationPeriod(Range.closedOpen(start, start.plusHours(1)))
                        .status(status)
                        .restaurantTable(statusTable)
                        .client(client)
                        .build();

                Reservation saved = reservationRepository.save(reservation);
                assertEquals(status, saved.getStatus());
            }
        }
    }

    @Nested
    @DisplayName("ReservationDish Cascade Tests")
    class CascadeTests {

        @Test
        @DisplayName("Should save Reservation with ReservationDishes (Cascade.ALL)")
        void shouldSaveReservationWithDishes() {
            Category cat = categoryRepository.save(Category.builder().name("Cascade Cat").build());

            Dish dish = dishRepository.save(Dish.builder()
                    .name("Cascade Dish")
                    .description("Test")
                    .price(new BigDecimal("20.00"))
                    .imageUrl("https://example.com/img.jpg")
                    .category(cat)
                    .build());

            LocalDateTime start = LocalDateTime.now().plusDays(5);

            Reservation reservation = Reservation.builder()
                    .reservationPeriod(Range.closedOpen(start, start.plusHours(2)))
                    .status(ReservationStatus.PENDING)
                    .restaurantTable(table)
                    .client(client)
                    .build();

            ReservationDish reservationDish = ReservationDish.builder()
                    .reservation(reservation)
                    .dish(dish)
                    .quantity(3)
                    .isServed(false)
                    .build();

            reservation.getReservationDishes().add(reservationDish);

            Reservation saved = reservationRepository.save(reservation);

            assertEquals(1, saved.getReservationDishes().size());

            ReservationDish savedDish = saved.getReservationDishes().iterator().next();
            assertEquals(3, savedDish.getQuantity());
            assertFalse(savedDish.isServed());
            assertNotNull(savedDish.getId());
        }
    }

    @Nested
    @DisplayName("Update and Delete Tests")
    class UpdateDeleteTests {

        @Test
        @DisplayName("Should update Reservation status")
        void shouldUpdateReservationStatus() {
            LocalDateTime start = LocalDateTime.now().plusDays(2);

            Reservation reservation = reservationRepository.save(Reservation.builder()
                    .reservationPeriod(Range.closedOpen(start, start.plusHours(2)))
                    .status(ReservationStatus.PENDING)
                    .restaurantTable(table)
                    .client(client)
                    .build());

            reservation.setStatus(ReservationStatus.CONFIRMED);
            reservation.setWaiter(waiter);
            reservationRepository.save(reservation);

            Reservation updated = reservationRepository.findById(reservation.getId()).orElseThrow();
            assertEquals(ReservationStatus.CONFIRMED, updated.getStatus());
            assertEquals(waiter.getId(), updated.getWaiter().getId());
        }

        @Test
        @DisplayName("Should delete Reservation")
        void shouldDeleteReservation() {
            LocalDateTime start = LocalDateTime.now().plusDays(10);

            Reservation reservation = reservationRepository.save(Reservation.builder()
                    .reservationPeriod(Range.closedOpen(start, start.plusHours(2)))
                    .status(ReservationStatus.CANCELLED)
                    .restaurantTable(table)
                    .client(client)
                    .build());

            reservationRepository.deleteById(reservation.getId());

            assertFalse(reservationRepository.existsById(reservation.getId()));
        }
    }
}