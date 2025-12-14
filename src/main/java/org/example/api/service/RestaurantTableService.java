package org.example.api.service;

import lombok.RequiredArgsConstructor;
import org.example.api.dto.RestaurantTableResponse;
import org.example.api.exception.ResourceAlreadyExistsException;
import org.example.api.exception.ResourceNotFoundException;
import org.example.api.model.RestaurantTable;
import org.example.api.repository.RestaurantTableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantTableService {

    private final RestaurantTableRepository tableRepository;

    public List<RestaurantTableResponse> findAll() {
        return tableRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    @Transactional
    public RestaurantTable create(RestaurantTable table) {
        boolean positionTaken = tableRepository.findAll().stream()
                .anyMatch(t -> t.getRowPosition().equals(table.getRowPosition()) &&
                               t.getColumnPosition().equals(table.getColumnPosition()));

        if (positionTaken) {
            throw new ResourceAlreadyExistsException("W tym miejscu stoi już inny stolik!");
        }

        return tableRepository.save(table);
    }

    @Transactional
    public void delete(Long id) {
        if (!tableRepository.existsById(id)) {
            throw new ResourceNotFoundException("Stolik nie istnieje");
        }

        tableRepository.deleteById(id);
    }

    private RestaurantTableResponse mapToResponse(RestaurantTable restaurantTable){
        return new RestaurantTableResponse(
                restaurantTable.getId(),
                restaurantTable.getTableNumber(),
                restaurantTable.getSeats(),
                restaurantTable.getRowPosition(),
                restaurantTable.getColumnPosition(),
                restaurantTable.getTableType()
        );
    }
}