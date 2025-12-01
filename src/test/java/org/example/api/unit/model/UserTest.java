package org.example.api.unit.model;

import org.example.api.model.Role;
import org.example.api.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Nested
    @DisplayName("Builder and Constructor Tests")
    class BuilderTests {

        @Test
        @DisplayName("Should create User using builder with all fields")
        void shouldCreateUserUsingBuilder() {
            User user = User.builder()
                    .id(1L)
                    .email("test@example.com")
                    .passwordHash("hashedPassword")
                    .firstName("Jan")
                    .lastName("Kowalski")
                    .phoneNumber("123456789")
                    .role(Role.ROLE_CLIENT)
                    .isActive(true)
                    .build();

            assertEquals(1L, user.getId());
            assertEquals("test@example.com", user.getEmail());
            assertEquals("hashedPassword", user.getPasswordHash());
            assertEquals("Jan", user.getFirstName());
            assertEquals("Kowalski", user.getLastName());
            assertEquals("123456789", user.getPhoneNumber());
            assertEquals(Role.ROLE_CLIENT, user.getRole());
            assertTrue(user.isActive());
        }

        @Test
        @DisplayName("Should have isActive default to true")
        void shouldHaveIsActiveDefaultToTrue() {
            User user = User.builder()
                    .email("test@example.com")
                    .build();

            assertTrue(user.isActive());
        }

        @Test
        @DisplayName("Should create User using no-args constructor")
        void shouldCreateUserUsingNoArgsConstructor() {
            User user = new User();
            assertNotNull(user);
            assertNull(user.getId());
        }
    }

    @Nested
    @DisplayName("Getters and Setters Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get all fields")
        void shouldSetAndGetAllFields() {
            User user = new User();
            
            user.setId(1L);
            user.setEmail("email@test.com");
            user.setPasswordHash("hash");
            user.setFirstName("Adam");
            user.setLastName("Nowak");
            user.setPhoneNumber("987654321");
            user.setRole(Role.ROLE_WAITER);
            user.setActive(false);

            assertEquals(1L, user.getId());
            assertEquals("email@test.com", user.getEmail());
            assertEquals("hash", user.getPasswordHash());
            assertEquals("Adam", user.getFirstName());
            assertEquals("Nowak", user.getLastName());
            assertEquals("987654321", user.getPhoneNumber());
            assertEquals(Role.ROLE_WAITER, user.getRole());
            assertFalse(user.isActive());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal when ids are the same")
        void shouldBeEqualWhenIdsAreSame() {
            User user1 = User.builder().id(1L).email("a@a.com").build();
            User user2 = User.builder().id(1L).email("b@b.com").build();

            assertEquals(user1, user2);
        }

        @Test
        @DisplayName("Should not be equal when ids are different")
        void shouldNotBeEqualWhenIdsAreDifferent() {
            User user1 = User.builder().id(1L).email("a@a.com").build();
            User user2 = User.builder().id(2L).email("a@a.com").build();

            assertNotEquals(user1, user2);
        }

        @Test
        @DisplayName("Should not be equal when id is null")
        void shouldNotBeEqualWhenIdIsNull() {
            User user1 = User.builder().id(null).build();
            User user2 = User.builder().id(1L).build();

            assertNotEquals(user1, user2);
        }

    }

    @Nested
    @DisplayName("Role Enum Tests")
    class RoleTests {

        @Test
        @DisplayName("Should assign ROLE_CLIENT")
        void shouldAssignRoleClient() {
            User user = User.builder().role(Role.ROLE_CLIENT).build();
            assertEquals(Role.ROLE_CLIENT, user.getRole());
        }

        @Test
        @DisplayName("Should assign ROLE_WAITER")
        void shouldAssignRoleWaiter() {
            User user = User.builder().role(Role.ROLE_WAITER).build();
            assertEquals(Role.ROLE_WAITER, user.getRole());
        }

        @Test
        @DisplayName("Should assign ROLE_ADMIN")
        void shouldAssignRoleAdmin() {
            User user = User.builder().role(Role.ROLE_ADMIN).build();
            assertEquals(Role.ROLE_ADMIN, user.getRole());
        }
    }
}