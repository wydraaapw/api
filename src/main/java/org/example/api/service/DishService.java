package org.example.api.service;

import lombok.RequiredArgsConstructor;
import org.example.api.dto.DishRequest;
import org.example.api.dto.DishResponse;
import org.example.api.exception.ResourceNotFoundException;
import org.example.api.model.Category;
import org.example.api.model.Dish;
import org.example.api.model.Ingredient;
import org.example.api.repository.CategoryRepository;
import org.example.api.repository.DishRepository;
import org.example.api.repository.IngredientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DishService {

    private final DishRepository dishRepository;
    private final CategoryRepository categoryRepository;
    private final IngredientRepository ingredientRepository;

    public List<DishResponse> findAll() {
        return dishRepository.findAllByIsActiveTrue().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public DishResponse create(DishRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Nieprawidłowe ID kategorii"));

        Set<Ingredient> ingredients = new HashSet<>();
        if (request.ingredientIds() != null && !request.ingredientIds().isEmpty()) {
            ingredients.addAll(ingredientRepository.findAllById(request.ingredientIds()));
        }

        Dish dish = Dish.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .imageUrl(request.imageUrl())
                .category(category)
                .ingredients(ingredients)
                .build();

        Dish saved = dishRepository.save(dish);
        return mapToResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Danie nie istnieje"));

        dish.setActive(false);
        dishRepository.save(dish);
    }

    private DishResponse mapToResponse(Dish dish) {
        return new DishResponse(
                dish.getId(),
                dish.getName(),
                dish.getDescription(),
                dish.getPrice(),
                dish.getImageUrl(),
                dish.getCategory().getName(),
                dish.getIngredients().stream().map(Ingredient::getName).toList()
        );
    }
}