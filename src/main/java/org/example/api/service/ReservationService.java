package org.example.api.service;

import io.hypersistence.utils.hibernate.type.range.Range;
import lombok.RequiredArgsConstructor;
import org.example.api.dto.*;
import org.example.api.exception.ReservationDateFromPastException;
import org.example.api.exception.ResourceAlreadyExistsException;
import org.example.api.exception.ResourceNotFoundException;
import org.example.api.exception.UserNotFoundException;
import org.example.api.model.*;
import org.example.api.repository.*;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RestaurantTableRepository tableRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final WorkShiftRepository workShiftRepository;
    private final DishRepository dishRepository;
    private final WaiterRepository waiterRepository;

    private static final Random random = new Random();

    public List<Long> getOccupiedTableIds(LocalDateTime start, LocalDateTime end) {
        return reservationRepository.findOverlappingReservations(start, end)
                .stream()
                .map(res -> res.getRestaurantTable().getId())
                .distinct()
                .toList();
    }

    @Transactional
    public void createReservation(String userEmail, ReservationRequest request) {
        if (request.start().isAfter(request.end())) {
            throw new IllegalArgumentException("Data rozpoczęcia musi być przed datą zakończenia.");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("Użytkownik nie istnieje."));

        Client client = clientRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono profilu klienta."));

        RestaurantTable table = tableRepository.findById(request.tableId())
                .orElseThrow(() -> new ResourceNotFoundException("Stolik nie istnieje."));

        List<Long> occupiedIds = getOccupiedTableIds(request.start(), request.end());
        if (occupiedIds.contains(table.getId())) {
            throw new ResourceAlreadyExistsException("Ten stolik jest już zajęty w wybranym terminie.");
        }

        if (request.start().isBefore(LocalDateTime.now())) {
            throw new ReservationDateFromPastException("Wybrano datę rezerwacji z przeszłości.");
        }

        Waiter assignedWaiter = findAvailableWaiter(request.start(), request.end());

        Reservation reservation = Reservation.builder()
                .client(client)
                .restaurantTable(table)
                .reservationPeriod(Range.closedOpen(request.start(), request.end()))
                .status(ReservationStatus.PENDING)
                .waiter(assignedWaiter)
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);

        if (request.dishes() != null && !request.dishes().isEmpty()) {
            for (ReservationDishRequest dishRequest : request.dishes()) {
                Dish dish = dishRepository.findById(dishRequest.dishId()).
                        orElseThrow(() -> new ResourceNotFoundException("Danie o podanym id nie istnieje."));

                ReservationDish reservationDish = ReservationDish.builder()
                        .reservation(savedReservation)
                        .dish(dish)
                        .quantity(dishRequest.quantity())
                        .isServed(false)
                        .build();

                savedReservation.getReservationDishes().add(reservationDish);
            }
            reservationRepository.save(savedReservation);
        }
    }

    private Waiter findAvailableWaiter(LocalDateTime start, LocalDateTime end) {
        List<WorkShift> availableShifts = workShiftRepository.findShiftsCoveringPeriod(start, end);

        if (availableShifts.isEmpty()) {
            return null;
        }

        WorkShift randomShift = availableShifts.get(random.nextInt(availableShifts.size()));
        return randomShift.getWaiter();
    }


    @Transactional
    public void updateStatus(Long reservationId, ReservationStatus newStatus) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
                () -> new ResourceNotFoundException("Rezerwacja z id " + reservationId + " nie istnieje."));

        reservation.setStatus(newStatus);
        reservationRepository.save(reservation);
    }

    public List<ReservationResponse> findAll() {
        return reservationRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    @Scheduled(cron = "0 */5 * * * *")
    public void autoCompleteReservations() {
        LocalDateTime treshold = LocalDateTime.now().minusMinutes(10);

        List<Reservation> expired = reservationRepository.findExpiredReservations(treshold);

        for (Reservation reservation : expired) {
            reservation.setStatus(ReservationStatus.COMPLETED);
        }

        if (!expired.isEmpty()) {
            reservationRepository.saveAll(expired);
        }
    }

    public List<WaiterResponse> getAvailableWaitersForReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Rezerwacja nie istnieje."));

        LocalDateTime start = reservation.getReservationPeriod().lower();
        LocalDateTime end = reservation.getReservationPeriod().upper();

        List<WorkShift> shifts = workShiftRepository.findShiftsCoveringPeriod(start, end);

        return shifts.stream()
                .map(shift -> {
                    User u = shift.getWaiter().getUser();
                    return new WaiterResponse(u.getId(), u.getEmail(), u.getFirstName(),
                            u.getLastName(), u.getPhoneNumber(), shift.getWaiter().isSpeaksEnglish(),
                            shift.getWaiter().getHireDate());
                })
                .toList();
    }

    @Transactional
    public void assignWaiter(Long reservationId, Long waiterId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Rezerwacja nie istnieje."));

        Waiter waiter = waiterRepository.findById(waiterId)
                .orElseThrow(() -> new ResourceNotFoundException("Kelner nie istnieje."));

        reservation.setWaiter(waiter);
        reservationRepository.save(reservation);
    }

    public List<ReservationResponse> findClientReservations(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Nie znaleziono użytkownika"));

        return reservationRepository.findAllByClientId(user.getId())
                .stream()
                .sorted((r1, r2) -> r2.getId().compareTo(r1.getId()))
                .map(this::mapToResponse)
                .toList();
    }


    private ReservationResponse mapToResponse(Reservation r) {
        LocalDateTime start = r.getReservationPeriod().lower();
        LocalDateTime end = r.getReservationPeriod().upper();

        String waiterName = (r.getWaiter() != null)
                ? r.getWaiter().getUser().getFirstName() + " " + r.getWaiter().getUser().getLastName()
                : "Nie przypisano";

        Long waiterId = (r.getWaiter() != null) ? r.getWaiter().getId() : null;

        List<ReservationDishResponse> dishesResponse = r.getReservationDishes().stream()
                .map(d -> new ReservationDishResponse(
                        d.getId(),
                        d.getDish().getName(),
                        d.getQuantity(),
                        d.isServed()
                ))
                .toList();

        return new ReservationResponse(
                r.getId(),
                r.getClient().getId(),
                r.getClient().getUser().getFirstName() + " " + r.getClient().getUser().getLastName(),
                r.getClient().getUser().getEmail(),
                r.getRestaurantTable().getId(),
                r.getRestaurantTable().getTableNumber(),
                r.getRestaurantTable().getSeats(),
                start,
                end,
                r.getStatus(),
                waiterId,
                waiterName,
                dishesResponse
        );
    }
}