package org.example.api.unit.model;

import org.example.api.model.Notification;
import org.example.api.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {

    @Nested
    @DisplayName("Builder and Constructor Tests")
    class BuilderTests {

        @Test
        @DisplayName("Should create Notification using builder")
        void shouldCreateNotificationUsingBuilder() {
            User user = User.builder().id(1L).email("test@test.com").build();
            LocalDateTime sentAt = LocalDateTime.of(2024, 1, 15, 10, 30);

            Notification notification = Notification.builder()
                    .id(1L)
                    .title("Reservation Confirmed")
                    .content("Your reservation for table 5 has been confirmed.")
                    .sentAt(sentAt)
                    .user(user)
                    .build();

            assertEquals(1L, notification.getId());
            assertEquals("Reservation Confirmed", notification.getTitle());
            assertEquals("Your reservation for table 5 has been confirmed.", notification.getContent());
            assertEquals(sentAt, notification.getSentAt());
            assertEquals(user, notification.getUser());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal when ids are the same")
        void shouldBeEqualWhenIdsAreSame() {
            Notification n1 = Notification.builder().id(1L).title("Title 1").build();
            Notification n2 = Notification.builder().id(1L).title("Title 2").build();

            assertEquals(n1, n2);
        }

        @Test
        @DisplayName("Should not be equal when ids are different")
        void shouldNotBeEqualWhenIdsAreDifferent() {
            Notification n1 = Notification.builder().id(1L).build();
            Notification n2 = Notification.builder().id(2L).build();

            assertNotEquals(n1, n2);
        }
    }
}