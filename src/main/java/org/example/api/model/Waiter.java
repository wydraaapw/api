package org.example.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "waiters")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Waiter {
    @Id
    private Long id;

    @Column(nullable = false)
    private LocalDate hireDate;

    @Column(nullable = false)
    private boolean speaksEnglish;

    @MapsId
    @JoinColumn(name = "id")
    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Waiter waiter &&
                this.id != null &&
                this.id.equals(waiter.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
