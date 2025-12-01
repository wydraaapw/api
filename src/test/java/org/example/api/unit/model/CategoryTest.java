package org.example.api.unit.model;

import org.example.api.model.Category;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    @Nested
    @DisplayName("Builder and Constructor Tests")
    class BuilderTests {

        @Test
        @DisplayName("Should create Category using builder")
        void shouldCreateCategoryUsingBuilder() {
            Category category = Category.builder()
                    .id(1L)
                    .name("Pizza")
                    .build();

            assertEquals(1L, category.getId());
            assertEquals("Pizza", category.getName());
        }

        @Test
        @DisplayName("Should create Category using no-args constructor")
        void shouldCreateCategoryUsingNoArgsConstructor() {
            Category category = new Category();
            assertNotNull(category);
            assertNull(category.getId());
            assertNull(category.getName());
        }

        @Test
        @DisplayName("Should create Category using all-args constructor")
        void shouldCreateCategoryUsingAllArgsConstructor() {
            Category category = new Category(1L, "Desserts");

            assertEquals(1L, category.getId());
            assertEquals("Desserts", category.getName());
        }
    }

    @Nested
    @DisplayName("Getters and Setters Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should set and get id")
        void shouldSetAndGetId() {
            Category category = new Category();
            category.setId(5L);
            assertEquals(5L, category.getId());
        }

        @Test
        @DisplayName("Should set and get name")
        void shouldSetAndGetName() {
            Category category = new Category();
            category.setName("Drinks");
            assertEquals("Drinks", category.getName());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal when ids are the same")
        void shouldBeEqualWhenIdsAreSame() {
            Category category1 = Category.builder().id(1L).name("Pizza").build();
            Category category2 = Category.builder().id(1L).name("Pasta").build();

            assertEquals(category1, category2);
        }

        @Test
        @DisplayName("Should not be equal when ids are different")
        void shouldNotBeEqualWhenIdsAreDifferent() {
            Category category1 = Category.builder().id(1L).name("Pizza").build();
            Category category2 = Category.builder().id(2L).name("Pizza").build();

            assertNotEquals(category1, category2);
        }

        @Test
        @DisplayName("Should not be equal when id is null")
        void shouldNotBeEqualWhenIdIsNull() {
            Category category1 = Category.builder().id(null).name("Pizza").build();
            Category category2 = Category.builder().id(1L).name("Pizza").build();

            assertNotEquals(category1, category2);
        }

        @Test
        @DisplayName("Should not be equal when both ids are null")
        void shouldNotBeEqualWhenBothIdsAreNull() {
            Category category1 = Category.builder().id(null).name("Pizza").build();
            Category category2 = Category.builder().id(null).name("Pizza").build();

            assertNotEquals(category1, category2);
        }

        @Test
        @DisplayName("Should not be equal to null")
        void shouldNotBeEqualToNull() {
            Category category = Category.builder().id(1L).name("Pizza").build();
            assertNotEquals(null, category);
        }

        @Test
        @DisplayName("HashCode should be consistent for same class")
        void hashCodeShouldBeConsistent() {
            Category category1 = Category.builder().id(1L).name("Pizza").build();
            Category category2 = Category.builder().id(2L).name("Pasta").build();

            assertEquals(category1.hashCode(), category2.hashCode());
        }
    }
}