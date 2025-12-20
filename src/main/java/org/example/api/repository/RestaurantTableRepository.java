package org.example.api.repository;

import org.example.api.model.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantTableRepository extends JpaRepository<org.example.api.model.RestaurantTable, Long> {

    List<RestaurantTable> findAllByIsActive(boolean isActive);

    boolean existsByTableNumberAndIsActiveTrue(Integer tableNumber);

    boolean existsByRowPositionAndColumnPositionAndIsActiveTrue(Integer rowPosition, Integer columnPosition);
}
