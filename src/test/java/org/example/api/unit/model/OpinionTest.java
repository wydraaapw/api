package org.example.api.unit.model;

import org.example.api.model.Client;
import org.example.api.model.Opinion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OpinionTest {

    @Nested
    @DisplayName("Builder and Constructor Tests")
    class BuilderTests {

        @Test
        @DisplayName("Should create Opinion using builder")
        void shouldCreateOpinionUsingBuilder() {
            Client client = Client.builder().id(1L).build();
            LocalDateTime createdAt = LocalDateTime.of(2024, 1, 20, 14, 0);

            Opinion opinion = Opinion.builder()
                    .id(1L)
                    .content("Great food and excellent service!")
                    .createdAt(createdAt)
                    .client(client)
                    .build();

            assertEquals(1L, opinion.getId());
            assertEquals("Great food and excellent service!", opinion.getContent());
            assertEquals(createdAt, opinion.getCreatedAt());
            assertEquals(client, opinion.getClient());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal when ids are the same")
        void shouldBeEqualWhenIdsAreSame() {
            Opinion o1 = Opinion.builder().id(1L).content("Content 1").build();
            Opinion o2 = Opinion.builder().id(1L).content("Content 2").build();

            assertEquals(o1, o2);
        }

        @Test
        @DisplayName("Should not be equal when ids are different")
        void shouldNotBeEqualWhenIdsAreDifferent() {
            Opinion o1 = Opinion.builder().id(1L).build();
            Opinion o2 = Opinion.builder().id(2L).build();

            assertNotEquals(o1, o2);
        }
    }
}