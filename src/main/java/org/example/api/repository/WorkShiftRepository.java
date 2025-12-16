package org.example.api.repository;

import org.example.api.model.WorkShift;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WorkShiftRepository extends JpaRepository<WorkShift, Long> {
    Page<WorkShift> findAllByWaiterId(Long waiterId, Pageable pageable);
    List<WorkShift> findAllByWaiterId(Long waiterId);
    boolean existsWorkShiftByWaiterId(Long waiterId);

    @Query(value = """
        SELECT ws.* FROM work_shifts ws
        WHERE ws.shift_period @> tsrange(?1, ?2)
    """, nativeQuery = true)
    List<WorkShift> findShiftsCoveringPeriod(LocalDateTime start, LocalDateTime end);
}
