package org.example.api.repository;

import org.example.api.model.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {
    List<Dish> findByCategoryId(Long categoryId);
    boolean existsByCategoryId(Long categoryId);
    boolean existsByIngredientsId(Long ingredientId);

}
