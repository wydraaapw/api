package org.example.api.unit.model;

import org.example.api.model.Category;
import org.example.api.model.Dish;
import org.example.api.model.Ingredient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class DishTest {

    @Nested
    @DisplayName("Builder and Constructor Tests")
    class BuilderTests {

        @Test
        @DisplayName("Should create Dish using builder")
        void shouldCreateDishUsingBuilder() {
            Category category = Category.builder().id(1L).name("Pizza").build();
            
            Dish dish = Dish.builder()
                    .id(1L)
                    .name("Margherita")
                    .description("Classic pizza with tomato and mozzarella")
                    .price(new BigDecimal("29.99"))
                    .imageUrl("https://img.com/margherita.jpg")
                    .category(category)
                    .build();

            assertEquals(1L, dish.getId());
            assertEquals("Margherita", dish.getName());
            assertEquals("Classic pizza with tomato and mozzarella", dish.getDescription());
            assertEquals(new BigDecimal("29.99"), dish.getPrice());
            assertEquals("https://img.com/margherita.jpg", dish.getImageUrl());
            assertEquals(category, dish.getCategory());
        }

        @Test
        @DisplayName("Should initialize ingredients as empty HashSet by default")
        void shouldInitializeIngredientsAsEmptyHashSet() {
            Dish dish = Dish.builder().build();

            assertNotNull(dish.getIngredients());
            assertTrue(dish.getIngredients().isEmpty());
            assertInstanceOf(HashSet.class, dish.getIngredients());
        }

        @Test
        @DisplayName("Should create Dish using no-args constructor")
        void shouldCreateDishUsingNoArgsConstructor() {
            Dish dish = new Dish();
            assertNotNull(dish);
            assertNull(dish.getId());
        }
    }

    @Nested
    @DisplayName("Ingredients Collection Tests")
    class IngredientsTests {

        @Test
        @DisplayName("Should add ingredient to dish")
        void shouldAddIngredientToDish() {
            Dish dish = Dish.builder().id(1L).build();
            Ingredient ingredient = Ingredient.builder().id(1L).name("Cheese").build();

            dish.getIngredients().add(ingredient);

            assertEquals(1, dish.getIngredients().size());
            assertTrue(dish.getIngredients().contains(ingredient));
        }

        @Test
        @DisplayName("Should add multiple ingredients to dish")
        void shouldAddMultipleIngredients() {
            Dish dish = Dish.builder().id(1L).build();
            Ingredient cheese = Ingredient.builder().id(1L).name("Cheese").build();
            Ingredient tomato = Ingredient.builder().id(2L).name("Tomato").build();

            dish.getIngredients().add(cheese);
            dish.getIngredients().add(tomato);

            assertEquals(2, dish.getIngredients().size());
        }

        @Test
        @DisplayName("Should remove ingredient from dish")
        void shouldRemoveIngredientFromDish() {
            Dish dish = Dish.builder().id(1L).build();
            Ingredient ingredient = Ingredient.builder().id(1L).name("Cheese").build();

            dish.getIngredients().add(ingredient);
            dish.getIngredients().remove(ingredient);

            assertTrue(dish.getIngredients().isEmpty());
        }
    }

    @Nested
    @DisplayName("Price Tests")
    class PriceTests {

        @Test
        @DisplayName("Should handle price with precision")
        void shouldHandlePriceWithPrecision() {
            Dish dish = Dish.builder()
                    .price(new BigDecimal("123.45"))
                    .build();

            assertEquals(new BigDecimal("123.45"), dish.getPrice());
        }

        @Test
        @DisplayName("Should handle price comparison correctly")
        void shouldHandlePriceComparisonCorrectly() {
            Dish dish1 = Dish.builder().price(new BigDecimal("10.00")).build();
            Dish dish2 = Dish.builder().price(new BigDecimal("10.00")).build();

            assertEquals(0, dish1.getPrice().compareTo(dish2.getPrice()));
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal when ids are the same")
        void shouldBeEqualWhenIdsAreSame() {
            Dish dish1 = Dish.builder().id(1L).name("Pizza").build();
            Dish dish2 = Dish.builder().id(1L).name("Pasta").build();

            assertEquals(dish1, dish2);
        }

        @Test
        @DisplayName("Should not be equal when ids are different")
        void shouldNotBeEqualWhenIdsAreDifferent() {
            Dish dish1 = Dish.builder().id(1L).build();
            Dish dish2 = Dish.builder().id(2L).build();

            assertNotEquals(dish1, dish2);
        }

        @Test
        @DisplayName("Should not be equal when id is null")
        void shouldNotBeEqualWhenIdIsNull() {
            Dish dish1 = Dish.builder().id(null).build();
            Dish dish2 = Dish.builder().id(1L).build();

            assertNotEquals(dish1, dish2);
        }
    }
}