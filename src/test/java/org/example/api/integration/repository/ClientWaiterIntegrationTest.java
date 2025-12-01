package org.example.api.integration.repository;

import org.example.api.model.*;
import org.example.api.repository.ClientRepository;
import org.example.api.repository.UserRepository;
import org.example.api.repository.WaiterRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Client & Waiter Integration Tests")
class ClientWaiterIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private WaiterRepository waiterRepository;

    @Nested
    @DisplayName("Client Tests")
    class ClientTests {

        @Test
        @DisplayName("Should save Client with User relationship (@MapsId)")
        void shouldSaveClientWithUserRelationship() {
            User user = User.builder()
                    .email("client@test.pl")
                    .passwordHash("hash")
                    .firstName("Klient")
                    .lastName("Testowy")
                    .phoneNumber("100200300")
                    .role(Role.ROLE_CLIENT)
                    .build();
            User savedUser = userRepository.save(user);

            Client client = Client.builder()
                    .user(savedUser)
                    .aboutMe("Lubię włoską kuchnię")
                    .allergies("Orzechy, gluten")
                    .build();
            Client savedClient = clientRepository.save(client);

            assertEquals(savedUser.getId(), savedClient.getId());

            Client found = clientRepository.findById(savedClient.getId()).orElseThrow();
            assertEquals("Lubię włoską kuchnię", found.getAboutMe());
            assertEquals("Orzechy, gluten", found.getAllergies());
        }

        @Test
        @DisplayName("Should allow null optional fields")
        void shouldAllowNullOptionalFields() {
            User user = User.builder()
                    .email("client.null@test.pl")
                    .passwordHash("hash")
                    .firstName("Null")
                    .lastName("Client")
                    .phoneNumber("999888777")
                    .role(Role.ROLE_CLIENT)
                    .build();
            userRepository.save(user);

            Client client = Client.builder()
                    .user(user)
                    .aboutMe(null)
                    .allergies(null)
                    .build();
            Client saved = clientRepository.save(client);

            assertNull(saved.getAboutMe());
            assertNull(saved.getAllergies());
        }

        @Test
        @DisplayName("Should update Client")
        void shouldUpdateClient() {
            User user = userRepository.save(User.builder()
                    .email("client.update@test.pl")
                    .passwordHash("hash")
                    .firstName("Update")
                    .lastName("Client")
                    .phoneNumber("111111111")
                    .role(Role.ROLE_CLIENT)
                    .build());

            Client client = clientRepository.save(Client.builder()
                    .user(user)
                    .aboutMe("Stary opis")
                    .build());

            client.setAboutMe("Nowy opis");
            client.setAllergies("Laktoza");
            clientRepository.save(client);

            Client updated = clientRepository.findById(client.getId()).orElseThrow();
            assertEquals("Nowy opis", updated.getAboutMe());
            assertEquals("Laktoza", updated.getAllergies());
        }
    }

    @Nested
    @DisplayName("Waiter Tests")
    class WaiterTests {

        @Test
        @DisplayName("Should save Waiter with User relationship (@MapsId)")
        void shouldSaveWaiterWithUserRelationship() {
            User user = User.builder()
                    .email("waiter@test.pl")
                    .passwordHash("hash")
                    .firstName("Kelner")
                    .lastName("Testowy")
                    .phoneNumber("555666777")
                    .role(Role.ROLE_WAITER)
                    .build();
            userRepository.save(user);

            Waiter waiter = Waiter.builder()
                    .user(user)
                    .hireDate(LocalDate.of(2023, 6, 1))
                    .speaksEnglish(true)
                    .build();
            Waiter saved = waiterRepository.save(waiter);

            assertEquals(user.getId(), saved.getId());
            assertEquals(LocalDate.of(2023, 6, 1), saved.getHireDate());
            assertTrue(saved.isSpeaksEnglish());
        }

        @Test
        @DisplayName("Should update Waiter")
        void shouldUpdateWaiter() {
            User user = userRepository.save(User.builder()
                    .email("waiter.update@test.pl")
                    .passwordHash("hash")
                    .firstName("Update")
                    .lastName("Waiter")
                    .phoneNumber("222222222")
                    .role(Role.ROLE_WAITER)
                    .build());

            Waiter waiter = waiterRepository.save(Waiter.builder()
                    .user(user)
                    .hireDate(LocalDate.of(2020, 1, 1))
                    .speaksEnglish(false)
                    .build());

            waiter.setSpeaksEnglish(true);
            waiterRepository.save(waiter);

            Waiter updated = waiterRepository.findById(waiter.getId()).orElseThrow();
            assertTrue(updated.isSpeaksEnglish());
        }
    }
}