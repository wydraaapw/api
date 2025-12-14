package org.example.api.repository;

import org.example.api.model.WorkShift;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkShiftRepository extends JpaRepository<WorkShift, Long> {
    Page<WorkShift> findAllByWaiterId(Long waiterId, Pageable pageable);
    List<WorkShift> findAllByWaiterId(Long waiterId);
    boolean existsWorkShiftByWaiterId(Long waiterId);
}
