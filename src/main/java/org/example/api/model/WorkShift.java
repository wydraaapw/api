package org.example.api.model;

import io.hypersistence.utils.hibernate.type.range.PostgreSQLRangeType;
import io.hypersistence.utils.hibernate.type.range.Range;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;

@Table(name = "work_shifts")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class WorkShift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "waiter_id", nullable = false)
    private Waiter waiter;

    @Type(PostgreSQLRangeType.class)
    @Column(nullable = false, name = "shift_period", columnDefinition = "tsrange")
    private Range<LocalDateTime> shiftPeriod;

    @Override
    public boolean equals(Object obj) {
        return obj instanceof WorkShift workShift &&
                this.id != null &&
                this.id.equals(workShift.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
