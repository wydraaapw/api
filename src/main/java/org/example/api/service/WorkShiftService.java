package org.example.api.service;

import io.hypersistence.utils.hibernate.type.range.Range;
import lombok.RequiredArgsConstructor;
import org.example.api.dto.WorkShiftRequest;
import org.example.api.dto.WorkShiftResponse;
import org.example.api.exception.ResourceAlreadyExistsException;
import org.example.api.exception.ResourceNotFoundException;
import org.example.api.model.Waiter;
import org.example.api.model.WorkShift;
import org.example.api.repository.WaiterRepository;
import org.example.api.repository.WorkShiftRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkShiftService {

    private final WorkShiftRepository workShiftRepository;
    private final WaiterRepository waiterRepository;

    public List<WorkShiftResponse> findAll() {
        return workShiftRepository.findAll().stream()
                .sorted(Comparator.comparing(shift -> shift.getShiftPeriod().lower()))
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public WorkShiftResponse create(WorkShiftRequest request) {
        if (request.start().isAfter(request.end())) {
            throw new IllegalArgumentException("Data rozpoczęcia musi być przed datą zakończenia");
        }

        Waiter waiter = waiterRepository.findById(request.waiterId())
                .orElseThrow(() -> new ResourceNotFoundException("Nie znaleziono kelnera"));

        List<WorkShift> waiterShifts = workShiftRepository.findAllByWaiterId(request.waiterId());

        boolean hasOverlap = waiterShifts.stream().anyMatch(shift -> {
            LocalDateTime existingStart = shift.getShiftPeriod().lower();
            LocalDateTime existingEnd = shift.getShiftPeriod().upper();

            return request.start().isBefore(existingEnd) && request.end().isAfter(existingStart);
        });

        if (hasOverlap) {
            throw new ResourceAlreadyExistsException("Ten kelner ma już zaplanowaną zmianę w tym terminie!");
        }

        WorkShift workShift = WorkShift.builder()
                .waiter(waiter)
                .shiftPeriod(Range.closedOpen(request.start(), request.end()))
                .build();

        return mapToResponse(workShiftRepository.save(workShift));
    }

    @Transactional
    public void delete(Long id) {
        workShiftRepository.deleteById(id);
    }

    private WorkShiftResponse mapToResponse(WorkShift shift) {
        return new WorkShiftResponse(
                shift.getId(),
                shift.getWaiter().getId(),
                shift.getWaiter().getUser().getFirstName() + " " + shift.getWaiter().getUser().getLastName(),
                shift.getShiftPeriod().lower(),
                shift.getShiftPeriod().upper()
        );
    }

    public Page<WorkShiftResponse> getAll(Long waiterId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<WorkShift> shifts;
        if (waiterId != null) {
            shifts = workShiftRepository.findAllByWaiterId(waiterId, pageable);
        } else {
            shifts = workShiftRepository.findAll(pageable);
        }

        return shifts.map(this::mapToResponse);
    }
}