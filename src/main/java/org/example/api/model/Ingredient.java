package org.example.api.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ingredients")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Ingredient ingredient &&
                this.id != null &&
                this.id.equals(ingredient.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
