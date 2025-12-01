package org.example.api.unit.model;

import org.example.api.model.Ingredient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IngredientTest {

    @Nested
    @DisplayName("Builder and Constructor Tests")
    class BuilderTests {

        @Test
        @DisplayName("Should create Ingredient using builder")
        void shouldCreateIngredientUsingBuilder() {
            Ingredient ingredient = Ingredient.builder()
                    .id(1L)
                    .name("Mozzarella")
                    .build();

            assertEquals(1L, ingredient.getId());
            assertEquals("Mozzarella", ingredient.getName());
        }

        @Test
        @DisplayName("Should create Ingredient using no-args constructor")
        void shouldCreateIngredientUsingNoArgsConstructor() {
            Ingredient ingredient = new Ingredient();
            assertNotNull(ingredient);
            assertNull(ingredient.getId());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal when ids are the same")
        void shouldBeEqualWhenIdsAreSame() {
            Ingredient i1 = Ingredient.builder().id(1L).name("Cheese").build();
            Ingredient i2 = Ingredient.builder().id(1L).name("Tomato").build();

            assertEquals(i1, i2);
        }

        @Test
        @DisplayName("Should not be equal when ids are different")
        void shouldNotBeEqualWhenIdsAreDifferent() {
            Ingredient i1 = Ingredient.builder().id(1L).build();
            Ingredient i2 = Ingredient.builder().id(2L).build();

            assertNotEquals(i1, i2);
        }
    }
}