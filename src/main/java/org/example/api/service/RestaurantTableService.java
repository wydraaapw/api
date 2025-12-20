package org.example.api.service;

import lombok.RequiredArgsConstructor;
import org.example.api.dto.RestaurantTableResponse;
import org.example.api.exception.ResourceAlreadyExistsException;
import org.example.api.exception.ResourceInUseException;
import org.example.api.exception.ResourceNotFoundException;
import org.example.api.exception.TableNumberNotUniqueException;
import org.example.api.model.ReservationStatus;
import org.example.api.model.RestaurantTable;
import org.example.api.model.TableType;
import org.example.api.repository.ReservationRepository;
import org.example.api.repository.RestaurantTableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RestaurantTableService {

    private final RestaurantTableRepository restaurantTableRepository;
    private final ReservationRepository reservationRepository;

    public List<RestaurantTableResponse> findAll() {
        return restaurantTableRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    public List<RestaurantTableResponse> findAllByIsActive(boolean isActive){
        return restaurantTableRepository.findAllByIsActive(isActive).stream().map(this::mapToResponse).toList();
    }

    @Transactional
    public RestaurantTable create(RestaurantTable table) {

        if (table.getTableType() == TableType.TABLE && table.getTableNumber() == null) {
            throw new IllegalArgumentException("Zwykły stolik musi posiadać numer!");
        }

        if (table.getTableType() == TableType.TABLE && restaurantTableRepository.existsByTableNumberAndIsActiveTrue(table.getTableNumber())){
            throw new TableNumberNotUniqueException("Stolik z numerem " + table.getTableNumber() + " juz istnieje");
        }

        if (restaurantTableRepository.existsByRowPositionAndColumnPositionAndIsActiveTrue(table.getRowPosition(), table.getColumnPosition())) {
            throw new ResourceAlreadyExistsException("W tym miejscu stoi już inny stolik!");
        }

        return restaurantTableRepository.save(table);
    }

    @Transactional
    public void delete(Long id) {
        RestaurantTable table = restaurantTableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stolik nie istnieje."));

        if (reservationRepository.existsByRestaurantTableIdAndStatusIn(
                id,
                Set.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED))) {
            throw new ResourceInUseException("Nie można usunąć stolika, który posiada aktywne rezerwacje.");
        }

        table.setActive(false);
        restaurantTableRepository.save(table);
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