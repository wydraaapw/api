package org.example.api.integration.repository;

import org.example.api.model.*;
import org.example.api.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Notification & Opinion Integration Tests")
class NotificationOpinionIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private OpinionRepository opinionRepository;

    private User user;
    private Client client;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.builder()
                .email("notif.user" + System.nanoTime() + "@test.pl")
                .passwordHash("hash")
                .firstName("Powiadomiony")
                .lastName("Użytkownik")
                .phoneNumber("666777888")
                .role(Role.ROLE_CLIENT)
                .build());

        client = clientRepository.save(Client.builder()
                .user(user)
                .aboutMe("Lubię opinie")
                .build());
    }

    @Nested
    @DisplayName("Notification Tests")
    class NotificationTests {

        @Test
        @DisplayName("Should save Notification for User")
        void shouldSaveNotificationForUser() {
            LocalDateTime sentAt = LocalDateTime.now();

            Notification notification = Notification.builder()
                    .title("Potwierdzenie rezerwacji")
                    .content("Twoja rezerwacja na stolik nr 5 została potwierdzona.")
                    .sentAt(sentAt)
                    .user(user)
                    .build();

            Notification saved = notificationRepository.save(notification);

            assertNotNull(saved.getId());
            assertEquals("Potwierdzenie rezerwacji", saved.getTitle());
            assertEquals(user.getId(), saved.getUser().getId());
        }

        @Test
        @DisplayName("Should save multiple notifications for one user")
        void shouldSaveMultipleNotificationsForOneUser() {
            notificationRepository.save(Notification.builder()
                    .title("Notification 1")
                    .content("Content 1")
                    .sentAt(LocalDateTime.now())
                    .user(user)
                    .build());

            notificationRepository.save(Notification.builder()
                    .title("Notification 2")
                    .content("Content 2")
                    .sentAt(LocalDateTime.now())
                    .user(user)
                    .build());

            List<Notification> all = notificationRepository.findAll();
            assertEquals(2, all.size());
        }

        @Test
        @DisplayName("Should update Notification")
        void shouldUpdateNotification() {
            Notification notification = notificationRepository.save(Notification.builder()
                    .title("Old Title")
                    .content("Old Content")
                    .sentAt(LocalDateTime.now())
                    .user(user)
                    .build());

            notification.setTitle("New Title");
            notification.setContent("New Content");
            notificationRepository.save(notification);

            Notification updated = notificationRepository.findById(notification.getId()).orElseThrow();
            assertEquals("New Title", updated.getTitle());
            assertEquals("New Content", updated.getContent());
        }

        @Test
        @DisplayName("Should delete Notification")
        void shouldDeleteNotification() {
            Notification notification = notificationRepository.save(Notification.builder()
                    .title("Delete Me")
                    .content("Content")
                    .sentAt(LocalDateTime.now())
                    .user(user)
                    .build());

            notificationRepository.deleteById(notification.getId());

            assertFalse(notificationRepository.existsById(notification.getId()));
        }
    }

    @Nested
    @DisplayName("Opinion Tests")
    class OpinionTests {

        @Test
        @DisplayName("Should save Opinion for Client")
        void shouldSaveOpinionForClient() {
            LocalDateTime createdAt = LocalDateTime.now();

            Opinion opinion = Opinion.builder()
                    .content("Świetna obsługa i pyszne jedzenie! Polecam!")
                    .createdAt(createdAt)
                    .client(client)
                    .build();

            Opinion saved = opinionRepository.save(opinion);

            assertNotNull(saved.getId());
            assertEquals("Świetna obsługa i pyszne jedzenie! Polecam!", saved.getContent());
            assertEquals(client.getId(), saved.getClient().getId());
        }

        @Test
        @DisplayName("Should save multiple opinions for one client")
        void shouldSaveMultipleOpinionsForOneClient() {
            opinionRepository.save(Opinion.builder()
                    .content("First opinion")
                    .createdAt(LocalDateTime.now())
                    .client(client)
                    .build());

            opinionRepository.save(Opinion.builder()
                    .content("Second opinion")
                    .createdAt(LocalDateTime.now())
                    .client(client)
                    .build());

            List<Opinion> all = opinionRepository.findAll();
            assertEquals(2, all.size());
        }

        @Test
        @DisplayName("Should update Opinion")
        void shouldUpdateOpinion() {
            Opinion opinion = opinionRepository.save(Opinion.builder()
                    .content("Original content")
                    .createdAt(LocalDateTime.now())
                    .client(client)
                    .build());

            opinion.setContent("Updated content");
            opinionRepository.save(opinion);

            Opinion updated = opinionRepository.findById(opinion.getId()).orElseThrow();
            assertEquals("Updated content", updated.getContent());
        }

        @Test
        @DisplayName("Should delete Opinion")
        void shouldDeleteOpinion() {
            Opinion opinion = opinionRepository.save(Opinion.builder()
                    .content("Delete me")
                    .createdAt(LocalDateTime.now())
                    .client(client)
                    .build());

            opinionRepository.deleteById(opinion.getId());

            assertFalse(opinionRepository.existsById(opinion.getId()));
        }
    }
}