package org.example.api.unit.model;

import org.example.api.model.Client;
import org.example.api.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {

    @Nested
    @DisplayName("Builder and Constructor Tests")
    class BuilderTests {

        @Test
        @DisplayName("Should create Client using builder")
        void shouldCreateClientUsingBuilder() {
            User user = User.builder().id(1L).email("test@test.com").build();
            
            Client client = Client.builder()
                    .id(1L)
                    .aboutMe("I love spicy food")
                    .allergies("Peanuts, Gluten")
                    .user(user)
                    .build();

            assertEquals(1L, client.getId());
            assertEquals("I love spicy food", client.getAboutMe());
            assertEquals("Peanuts, Gluten", client.getAllergies());
            assertEquals(user, client.getUser());
        }

        @Test
        @DisplayName("Should allow null for optional fields")
        void shouldAllowNullForOptionalFields() {
            Client client = Client.builder()
                    .id(1L)
                    .aboutMe(null)
                    .allergies(null)
                    .build();

            assertNull(client.getAboutMe());
            assertNull(client.getAllergies());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal when ids are the same")
        void shouldBeEqualWhenIdsAreSame() {
            Client c1 = Client.builder().id(1L).aboutMe("About 1").build();
            Client c2 = Client.builder().id(1L).aboutMe("About 2").build();

            assertEquals(c1, c2);
        }

        @Test
        @DisplayName("Should not be equal when ids are different")
        void shouldNotBeEqualWhenIdsAreDifferent() {
            Client c1 = Client.builder().id(1L).build();
            Client c2 = Client.builder().id(2L).build();

            assertNotEquals(c1, c2);
        }
    }
}