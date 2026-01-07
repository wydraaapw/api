package org.example.api.service;

import io.hypersistence.utils.hibernate.type.range.Range;
import lombok.RequiredArgsConstructor;
import org.example.api.dto.*;
import org.example.api.exception.IncorrectReservationDateException;
import org.example.api.exception.ResourceAlreadyExistsException;
import org.example.api.exception.ResourceNotFoundException;
import org.example.api.exception.UserNotFoundException;
import org.example.api.model.*;
import org.example.api.repository.*;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
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
    private final WaiterRepository waiterRepository;

    private static final Random random = new Random();
    private static final LocalTime LAST_ALLOWED_START_TIME = LocalTime.of(19, 0);
    private static final LocalTime CLOSING_TIME = LocalTime.of(22, 0);

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

        if (request.start().isBefore(LocalDateTime.now())) {
            throw new IncorrectReservationDateException("Wybrano datę rezerwacji z przeszłości.");
        }

        LocalTime startTime = request.start().toLocalTime();
        LocalTime endTime = request.end().toLocalTime();

        if (startTime.isAfter(LAST_ALLOWED_START_TIME) || endTime.isAfter(CLOSING_TIME)) {
            throw new IncorrectReservationDateException(
                    "Rezerwacja możliwa najpóźniej na 19:00 i musi zakończyć się do 22:00."
            );
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

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("Nie można edytować anulowanej rezerwacji.");
        }

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
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(10);

        List<Reservation> expired = reservationRepository.findExpiredReservations(threshold);

        for (Reservation reservation : expired) {
            reservation.setStatus(ReservationStatus.COMPLETED);
        }

        if (!expired.isEmpty()) {
            reservationRepository.saveAll(expired);
        }
    }

    @Transactional
    @Scheduled(cron = "0 */5 * * * *")
    public void autoCancelNotConfirmedReservations(){
        List<Reservation> reservations = reservationRepository.findNotConfirmedReservations(LocalDateTime.now());

        reservations.forEach(reservation -> reservation.setStatus(ReservationStatus.CANCELLED));

        reservationRepository.saveAll(reservations);
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

        Client client = clientRepository.findById(user.getId()).orElseThrow(
                () -> new AccessDeniedException("Zalogowany użytkownik nie jest klientem.")
        );

        return reservationRepository.findAllByClientId(client.getId())
                .stream()
                .sorted((r1, r2) -> r2.getId().compareTo(r1.getId()))
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public void cancelReservation(Long id, String userEmail){
        Reservation reservation = reservationRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Rezerwacja o podanym id nie istnieje")
        );

        if (!reservation.getClient().getUser().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("Nie masz uprawnień do anulowania tej rezerwacji.");
        }

        if (reservation.getStatus() == ReservationStatus.PENDING || reservation.getStatus() == ReservationStatus.CONFIRMED) {
            reservation.setStatus(ReservationStatus.CANCELLED);
            reservationRepository.save(reservation);
        } else {
            throw new IllegalStateException("Nie można anulować rezerwacji o statusie: " + reservation.getStatus());
        }
    }

    public List<ReservationResponse> findWaiterReservations(String email){
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("Uzytkownik z podanym email nie istnieje."));

        Waiter waiter = waiterRepository.findById(user.getId()).orElseThrow(
                () -> new AccessDeniedException("Zalogowany użytkownik nie jest kelnerem")
        );

        return reservationRepository.findAllByWaiterId(waiter.getId())
                .stream()
                .sorted((r1, r2) -> r2.getReservationPeriod().lower().compareTo(r1.getReservationPeriod().lower()))
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public void updateReservationDishStatus(Long reservationId, Long reservationDishId, String email){
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
                () -> new ResourceNotFoundException("Rezerwacja o podanym id nie istnieje")
        );

        if (reservation.getWaiter() == null || !reservation.getWaiter().getUser().getEmail().equals(email)){
            throw new AccessDeniedException("Nie obsługujesz tej rezerwacji.");
        }

        ReservationDish reservationDish = reservation.getReservationDishes()
                .stream()
                .filter(dish -> Objects.equals(dish.getId(), reservationDishId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Danie o id " + reservationDishId + "nie jest daniem tej rezerwacji"));

        reservationDish.setServed(!reservationDish.isServed());
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