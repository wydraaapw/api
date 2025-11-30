package org.example.api.model;

import io.hypersistence.utils.hibernate.type.range.PostgreSQLRangeType;
import io.hypersistence.utils.hibernate.type.range.Range;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "reservations")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Type(PostgreSQLRangeType.class)
    @Column(nullable = false, name = "reservation_period", columnDefinition = "tsrange")
    private Range<LocalDateTime> reservationPeriod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReservationStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "table_id", nullable = false)
    private RestaurantTable restaurantTable;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "waiter_id")
    private Waiter waiter;

    @Builder.Default
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReservationDish> reservationDishes = new HashSet<>();

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Reservation reservation &&
                this.id != null &&
                this.id.equals(reservation.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
