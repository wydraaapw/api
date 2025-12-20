package org.example.api.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "restaurant_tables")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class RestaurantTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TableType tableType = TableType.TABLE;

    private Integer tableNumber;

    @Column
    private Integer seats;

    @Column(nullable = false)
    private Integer rowPosition;

    @Column(nullable = false)
    private Integer columnPosition;

    @Column(nullable = false)
    @Builder.Default
    private boolean isActive = true;

    @Override
    public boolean equals(Object obj) {
        return obj instanceof RestaurantTable restaurantTable &&
                this.id != null &&
                this.id.equals(restaurantTable.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}