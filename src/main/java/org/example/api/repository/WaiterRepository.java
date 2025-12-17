package org.example.api.repository;

import org.example.api.model.Waiter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WaiterRepository extends JpaRepository<Waiter, Long> {
    @Query("SELECT w FROM Waiter w JOIN w.user u WHERE u.isActive = true")
    List<Waiter> findAllActive();
}
