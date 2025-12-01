package org.example.api.integration.repository;

import org.example.api.model.Category;
import org.example.api.model.Dish;
import org.example.api.model.Ingredient;
import org.example.api.repository.CategoryRepository;
import org.example.api.repository.DishRepository;
import org.example.api.repository.IngredientRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Menu Integration Tests (Category, Ingredient, Dish)")
class MenuIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private DishRepository dishRepository;

    @Nested
    @DisplayName("Category Tests")
    class CategoryTests {

        @Test
        @DisplayName("Should save and retrieve Category")
        void shouldSaveAndRetrieveCategory() {
            Category category = Category.builder()
                    .name("Pizza")
                    .build();
            Category saved = categoryRepository.save(category);

            assertNotNull(saved.getId());
            assertEquals("Pizza", saved.getName());
        }

        @Test
        @DisplayName("Should enforce unique category name")
        void shouldEnforceUniqueCategoryName() {
            categoryRepository.saveAndFlush(Category.builder().name("Unique").build());

            Category duplicate = Category.builder().name("Unique").build();
            assertThrows(Exception.class, () -> categoryRepository.saveAndFlush(duplicate));
        }

        @Test
        @DisplayName("Should update Category")
        void shouldUpdateCategory() {
            Category category = categoryRepository.save(
                    Category.builder().name("Old Name").build()
            );

            category.setName("New Name");
            categoryRepository.save(category);

            Category updated = categoryRepository.findById(category.getId()).orElseThrow();
            assertEquals("New Name", updated.getName());
        }
    }

    @Nested
    @DisplayName("Ingredient Tests")
    class IngredientTests {

        @Test
        @DisplayName("Should save and retrieve Ingredient")
        void shouldSaveAndRetrieveIngredient() {
            Ingredient ingredient = Ingredient.builder()
                    .name("Mozzarella")
                    .build();
            Ingredient saved = ingredientRepository.save(ingredient);

            assertNotNull(saved.getId());
            assertEquals("Mozzarella", saved.getName());
        }

        @Test
        @DisplayName("Should enforce unique ingredient name")
        void shouldEnforceUniqueIngredientName() {
            ingredientRepository.saveAndFlush(Ingredient.builder().name("Cheese").build());

            Ingredient duplicate = Ingredient.builder().name("Cheese").build();
            assertThrows(Exception.class, () -> ingredientRepository.saveAndFlush(duplicate));
        }
    }

    @Nested
    @DisplayName("Dish Tests")
    class DishTests {

        @Test
        @DisplayName("Should save Dish with Category")
        void shouldSaveDishWithCategory() {
            Category category = categoryRepository.save(
                    Category.builder().name("Makarony").build()
            );

            Dish dish = Dish.builder()
                    .name("Spaghetti Bolognese")
                    .description("Klasyczne spaghetti z sosem mięsnym")
                    .price(new BigDecimal("32.99"))
                    .imageUrl("https://example.com/spaghetti.jpg")
                    .category(category)
                    .build();

            Dish saved = dishRepository.save(dish);

            assertNotNull(saved.getId());
            assertEquals("Spaghetti Bolognese", saved.getName());
            assertEquals(category.getId(), saved.getCategory().getId());
        }

        @Test
        @DisplayName("Should save Dish with Ingredients (ManyToMany)")
        void shouldSaveDishWithIngredients() {
            Category category = categoryRepository.save(
                    Category.builder().name("Sałatki").build()
            );

            Ingredient lettuce = ingredientRepository.save(
                    Ingredient.builder().name("Sałata").build()
            );
            Ingredient tomato = ingredientRepository.save(
                    Ingredient.builder().name("Pomidor").build()
            );
            Ingredient cucumber = ingredientRepository.save(
                    Ingredient.builder().name("Ogórek").build()
            );

            Dish dish = Dish.builder()
                    .name("Sałatka Grecka")
                    .description("Świeża sałatka")
                    .price(new BigDecimal("24.00"))
                    .imageUrl("https://example.com/salad.jpg")
                    .category(category)
                    .build();

            dish.getIngredients().addAll(Set.of(lettuce, tomato, cucumber));

            Dish saved = dishRepository.save(dish);

            assertEquals(3, saved.getIngredients().size());
            assertTrue(saved.getIngredients().contains(lettuce));
        }

        @Test
        @DisplayName("Should handle price precision correctly")
        void shouldHandlePricePrecision() {
            Category cat = categoryRepository.save(Category.builder().name("Desery").build());

            Dish dish = Dish.builder()
                    .name("Tiramisu")
                    .description("Włoski deser")
                    .price(new BigDecimal("24.50"))
                    .imageUrl("https://example.com/tiramisu.jpg")
                    .category(cat)
                    .build();

            Dish saved = dishRepository.save(dish);
            assertEquals(0, new BigDecimal("24.50").compareTo(saved.getPrice()));
        }

        @Test
        @DisplayName("Should update Dish and its ingredients")
        void shouldUpdateDishAndIngredients() {
            Category cat = categoryRepository.save(Category.builder().name("Update Cat").build());
            Ingredient ing1 = ingredientRepository.save(Ingredient.builder().name("Ing1").build());
            Ingredient ing2 = ingredientRepository.save(Ingredient.builder().name("Ing2").build());

            Dish dish = Dish.builder()
                    .name("Original")
                    .description("Desc")
                    .price(new BigDecimal("10.00"))
                    .imageUrl("https://example.com/img.jpg")
                    .category(cat)
                    .build();
            dish.getIngredients().add(ing1);
            dish = dishRepository.save(dish);

            dish.setName("Updated");
            dish.getIngredients().clear();
            dish.getIngredients().add(ing2);
            dishRepository.save(dish);

            Dish updated = dishRepository.findById(dish.getId()).orElseThrow();
            assertEquals("Updated", updated.getName());
            assertEquals(1, updated.getIngredients().size());
            assertTrue(updated.getIngredients().contains(ing2));
        }
    }
}