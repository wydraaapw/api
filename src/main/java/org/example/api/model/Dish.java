package org.example.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "dishes")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Dish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Dish dish &&
                this.id != null &&
                this.id.equals(dish.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
