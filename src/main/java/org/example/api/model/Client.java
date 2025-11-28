package org.example.api.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Objects;

@Entity
@Table(name = "clients")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Client {
    @Id
    private Long id;

    @Column(length = 500)
    private String aboutMe;

    @Column(length = 500)
    private String allergies;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    @MapsId
    private User user;

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Client client &&
                this.id != null &&
                this.id.equals(client.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
