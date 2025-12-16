package org.example.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantTableRepository extends JpaRepository<org.example.api.model.RestaurantTable, Long> {
    boolean existsRestaurantTableByTableNumber(Integer id);
}
