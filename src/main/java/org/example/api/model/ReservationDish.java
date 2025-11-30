package org.example.api.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reservation_dishes")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ReservationDish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer quantity;

    @Builder.Default
    @Column(nullable = false)
    private boolean isServed = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dish_id", nullable = false)
    private Dish dish;

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ReservationDish reservationDish &&
                this.id != null &&
                this.id.equals(reservationDish.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
