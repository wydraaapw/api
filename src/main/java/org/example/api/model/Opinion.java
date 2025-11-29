package org.example.api.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Table(name = "opinions")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Opinion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Opinion opinion &&
                this.id != null &&
                this.id.equals(opinion.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
