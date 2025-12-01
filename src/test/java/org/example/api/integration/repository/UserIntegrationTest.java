package org.example.api.integration.repository;

import org.example.api.model.Role;
import org.example.api.model.User;
import org.example.api.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Integration Tests")
class UserIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should save and retrieve User")
    void shouldSaveAndRetrieveUser() {
        User user = User.builder()
                .email("jan.kowalski@test.pl")
                .passwordHash("$2a$10$hashedPassword")
                .firstName("Jan")
                .lastName("Kowalski")
                .phoneNumber("123456789")
                .role(Role.ROLE_CLIENT)
                .build();

        User saved = userRepository.save(user);

        assertNotNull(saved.getId());
        assertTrue(saved.isActive());

        Optional<User> found = userRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("jan.kowalski@test.pl", found.get().getEmail());
    }

    @Test
    @DisplayName("Should find User by email")
    void shouldFindUserByEmail() {
        User user = User.builder()
                .email("unique@email.pl")
                .passwordHash("hash")
                .firstName("Adam")
                .lastName("Nowak")
                .phoneNumber("987654321")
                .role(Role.ROLE_ADMIN)
                .build();
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("unique@email.pl");

        assertTrue(found.isPresent());
        assertEquals("Adam", found.get().getFirstName());
    }

    @Test
    @DisplayName("Should check if email exists")
    void shouldCheckIfEmailExists() {
        User user = User.builder()
                .email("exists@test.pl")
                .passwordHash("hash")
                .firstName("Test")
                .lastName("User")
                .phoneNumber("111222333")
                .role(Role.ROLE_CLIENT)
                .build();
        userRepository.save(user);

        assertTrue(userRepository.existsByEmail("exists@test.pl"));
        assertFalse(userRepository.existsByEmail("notexists@test.pl"));
    }

    @Test
    @DisplayName("Should enforce unique email constraint")
    void shouldEnforceUniqueEmailConstraint() {
        User user1 = User.builder()
                .email("duplicate@test.pl")
                .passwordHash("hash1")
                .firstName("First")
                .lastName("User")
                .phoneNumber("111111111")
                .role(Role.ROLE_CLIENT)
                .build();
        userRepository.saveAndFlush(user1);

        User user2 = User.builder()
                .email("duplicate@test.pl")
                .passwordHash("hash2")
                .firstName("Second")
                .lastName("User")
                .phoneNumber("222222222")
                .role(Role.ROLE_CLIENT)
                .build();

        assertThrows(Exception.class, () -> userRepository.saveAndFlush(user2));
    }

    @Test
    @DisplayName("Should update User")
    void shouldUpdateUser() {
        User user = User.builder()
                .email("update@test.pl")
                .passwordHash("oldHash")
                .firstName("Old")
                .lastName("Name")
                .phoneNumber("123123123")
                .role(Role.ROLE_CLIENT)
                .build();
        User saved = userRepository.save(user);

        saved.setFirstName("New");
        saved.setLastName("Updated");
        saved.setActive(false);
        userRepository.save(saved);

        User updated = userRepository.findById(saved.getId()).orElseThrow();
        assertEquals("New", updated.getFirstName());
        assertEquals("Updated", updated.getLastName());
        assertFalse(updated.isActive());
    }

    @Test
    @DisplayName("Should delete User")
    void shouldDeleteUser() {
        User user = User.builder()
                .email("delete@test.pl")
                .passwordHash("hash")
                .firstName("Delete")
                .lastName("Me")
                .phoneNumber("999999999")
                .role(Role.ROLE_CLIENT)
                .build();
        User saved = userRepository.save(user);

        userRepository.deleteById(saved.getId());

        assertFalse(userRepository.existsById(saved.getId()));
    }
}