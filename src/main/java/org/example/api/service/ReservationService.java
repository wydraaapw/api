package org.example.api.service;

import io.hypersistence.utils.hibernate.type.range.Range;
import lombok.RequiredArgsConstructor;
import org.example.api.dto.ReservationDishRequest;
import org.example.api.dto.ReservationRequest;
import org.example.api.exception.ReservationDateFromPastException;
import org.example.api.exception.ResourceAlreadyExistsException;
import org.example.api.exception.ResourceNotFoundException;
import org.example.api.exception.UserNotFoundException;
import org.example.api.model.*;
import org.example.api.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RestaurantTableRepository tableRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final WorkShiftRepository workShiftRepository;
    private final DishRepository dishRepository;

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

        if (request.start().isBefore(LocalDateTime.now())){
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

        if (request.dishes() != null && !request.dishes().isEmpty()){
            for (ReservationDishRequest dishRequest :  request.dishes()){
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
}