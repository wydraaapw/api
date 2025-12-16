package org.example.api.repository;

import org.example.api.model.Reservation;
import org.example.api.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query(value = """
        SELECT r.* FROM reservations r
        WHERE r.status IN ('PENDING', 'CONFIRMED', 'IN_PROGRESS')
        AND r.reservation_period && tsrange(?1, ?2)
    """, nativeQuery = true)
    List<Reservation> findOverlappingReservations(LocalDateTime start, LocalDateTime end);

    List<Reservation> findAllByClientId(Long clientId);

    boolean existsByRestaurantTableIdAndStatusIn(Long tableId, Set<ReservationStatus> statuses);
}
