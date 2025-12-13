package org.example.api.service;

import lombok.RequiredArgsConstructor;
import org.example.api.dto.IngredientRequest;
import org.example.api.exception.ResourceAlreadyExistsException;
import org.example.api.exception.ResourceInUseException;
import org.example.api.model.Ingredient;
import org.example.api.repository.DishRepository;
import org.example.api.repository.IngredientRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final DishRepository dishRepository;


    public List<Ingredient> findAll() {
        return ingredientRepository.findAll();
    }

    public Ingredient create(IngredientRequest request) {
        if (ingredientRepository.existsByName(request.name())) {
            throw new ResourceAlreadyExistsException("Składnik o nazwie " + request.name() + " już istnieje.");
        }

        return ingredientRepository.save(Ingredient.builder()
                .name(request.name())
                .build());
    }

    public void delete(Long id) {
        if (dishRepository.existsByIngredientsId(id)) {
            throw new ResourceInUseException("Nie można usunąć składnika, ponieważ jest używany w jednym lub wielu daniach.");
        }

        ingredientRepository.deleteById(id);
    }
}
