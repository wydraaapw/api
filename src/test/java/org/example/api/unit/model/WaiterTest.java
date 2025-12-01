package org.example.api.unit.model;

import org.example.api.model.Role;
import org.example.api.model.User;
import org.example.api.model.Waiter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class WaiterTest {

    @Nested
    @DisplayName("Builder and Constructor Tests")
    class BuilderTests {

        @Test
        @DisplayName("Should create Waiter using builder")
        void shouldCreateWaiterUsingBuilder() {
            User user = User.builder().id(1L).email("waiter@test.com").role(Role.ROLE_WAITER).build();
            LocalDate hireDate = LocalDate.of(2023, 5, 15);

            Waiter waiter = Waiter.builder()
                    .id(1L)
                    .hireDate(hireDate)
                    .speaksEnglish(true)
                    .user(user)
                    .build();

            assertEquals(1L, waiter.getId());
            assertEquals(hireDate, waiter.getHireDate());
            assertTrue(waiter.isSpeaksEnglish());
            assertEquals(user, waiter.getUser());
        }

        @Test
        @DisplayName("Should create Waiter with speaksEnglish false")
        void shouldCreateWaiterWithSpeaksEnglishFalse() {
            Waiter waiter = Waiter.builder()
                    .id(1L)
                    .speaksEnglish(false)
                    .build();

            assertFalse(waiter.isSpeaksEnglish());
        }
    }

    @Nested
    @DisplayName("Getters and Setters Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get hireDate")
        void shouldSetAndGetHireDate() {
            Waiter waiter = new Waiter();
            LocalDate date = LocalDate.of(2024, 1, 1);
            waiter.setHireDate(date);
            assertEquals(date, waiter.getHireDate());
        }

        @Test
        @DisplayName("Should set and get speaksEnglish")
        void shouldSetAndGetSpeaksEnglish() {
            Waiter waiter = new Waiter();
            waiter.setSpeaksEnglish(true);
            assertTrue(waiter.isSpeaksEnglish());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal when ids are the same")
        void shouldBeEqualWhenIdsAreSame() {
            Waiter w1 = Waiter.builder().id(1L).speaksEnglish(true).build();
            Waiter w2 = Waiter.builder().id(1L).speaksEnglish(false).build();

            assertEquals(w1, w2);
        }

        @Test
        @DisplayName("Should not be equal when ids are different")
        void shouldNotBeEqualWhenIdsAreDifferent() {
            Waiter w1 = Waiter.builder().id(1L).build();
            Waiter w2 = Waiter.builder().id(2L).build();

            assertNotEquals(w1, w2);
        }
    }
}