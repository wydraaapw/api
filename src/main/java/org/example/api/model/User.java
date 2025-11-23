package org.example.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "users")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email(message = "ERROR_EMAIL_INVALID")
    @NotBlank(message = "ERROR_EMAIL_REQUIRED")
    @Column(nullable = false, unique = true)
    private String email;


    @NotBlank(message = "ERROR_PASSWORD_REQUIRED")
    @Column(nullable = false)
    private String passwordHash;

    @NotBlank(message = "ERROR_FIRSTNAME_REQUIRED")
    @Size(min = 2, max = 30, message = "ERROR_FIRSTNAME_INVALID")
    @Column(length = 30, nullable = false)
    private String firstName;

    @NotBlank(message = "ERROR_LASTNAME_REQUIRED")
    @Size(min = 2, max = 30, message = "ERROR_LASTNAME_INVALID")
    @Column(length = 30, nullable = false)
    private String lastName;

    @NotBlank(message = "ERROR_PHONE_NUMBER_REQUIRED")
    @Pattern(
            regexp = "^\\d{9}$",
            message = "ERROR_PHONE_NUMBER_INVALID"
    )
    @Column(length = 9, nullable = false)
    private String phoneNumber;

    @NotNull(message = "ERROR_ROLE_REQUIRED")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Role role;

    @Column(nullable = false)
    @Builder.Default
    private boolean isActive = true;

    @Override
    public boolean equals(Object obj) {
        return obj instanceof User user && Objects.equals(user.getId(), this.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
