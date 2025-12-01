package org.example.api.integration.repository;

import io.hypersistence.utils.hibernate.type.range.Range;
import org.example.api.model.Role;
import org.example.api.model.User;
import org.example.api.model.Waiter;
import org.example.api.model.WorkShift;
import org.example.api.repository.UserRepository;
import org.example.api.repository.WaiterRepository;
import org.example.api.repository.WorkShiftRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("WorkShift Integration Tests")
class WorkShiftIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WaiterRepository waiterRepository;

    @Autowired
    private WorkShiftRepository workShiftRepository;

    private Waiter waiter;

    @BeforeEach
    void setUp() {
        User user = userRepository.save(User.builder()
                .email("shift.waiter" + System.nanoTime() + "@test.pl")
                .passwordHash("hash")
                .firstName("Zmianowy")
                .lastName("Kelner")
                .phoneNumber("333444555")
                .role(Role.ROLE_WAITER)
                .build());

        waiter = waiterRepository.save(Waiter.builder()
                .user(user)
                .hireDate(LocalDate.of(2022, 1, 15))
                .speaksEnglish(true)
                .build());
    }

    @Test
    @DisplayName("Should save WorkShift with tsrange")
    void shouldSaveWorkShiftWithTsrange() {
        LocalDateTime shiftStart = LocalDateTime.of(2024, 12, 10, 8, 0);
        LocalDateTime shiftEnd = LocalDateTime.of(2024, 12, 10, 16, 0);

        WorkShift workShift = WorkShift.builder()
                .waiter(waiter)
                .shiftPeriod(Range.closedOpen(shiftStart, shiftEnd))
                .build();

        WorkShift saved = workShiftRepository.save(workShift);

        assertNotNull(saved.getId());
        assertEquals(shiftStart, saved.getShiftPeriod().lower());
        assertEquals(shiftEnd, saved.getShiftPeriod().upper());
        assertEquals(waiter.getId(), saved.getWaiter().getId());
    }

    @Test
    @DisplayName("Should save multiple shifts for one waiter")
    void shouldSaveMultipleShiftsForOneWaiter() {
        LocalDateTime day1Start = LocalDateTime.of(2024, 12, 11, 8, 0);
        LocalDateTime day2Start = LocalDateTime.of(2024, 12, 12, 8, 0);

        workShiftRepository.save(WorkShift.builder()
                .waiter(waiter)
                .shiftPeriod(Range.closedOpen(day1Start, day1Start.plusHours(8)))
                .build());

        workShiftRepository.save(WorkShift.builder()
                .waiter(waiter)
                .shiftPeriod(Range.closedOpen(day2Start, day2Start.plusHours(8)))
                .build());

        List<WorkShift> allShifts = workShiftRepository.findAll();
        assertEquals(2, allShifts.size());
    }

    @Test
    @DisplayName("Should update WorkShift")
    void shouldUpdateWorkShift() {
        LocalDateTime originalStart = LocalDateTime.of(2024, 12, 15, 10, 0);
        LocalDateTime newStart = LocalDateTime.of(2024, 12, 15, 12, 0);

        WorkShift shift = workShiftRepository.save(WorkShift.builder()
                .waiter(waiter)
                .shiftPeriod(Range.closedOpen(originalStart, originalStart.plusHours(8)))
                .build());

        shift.setShiftPeriod(Range.closedOpen(newStart, newStart.plusHours(6)));
        workShiftRepository.save(shift);

        WorkShift updated = workShiftRepository.findById(shift.getId()).orElseThrow();
        assertEquals(newStart, updated.getShiftPeriod().lower());
    }

    @Test
    @DisplayName("Should delete WorkShift")
    void shouldDeleteWorkShift() {
        LocalDateTime start = LocalDateTime.of(2024, 12, 20, 8, 0);

        WorkShift shift = workShiftRepository.save(WorkShift.builder()
                .waiter(waiter)
                .shiftPeriod(Range.closedOpen(start, start.plusHours(8)))
                .build());

        workShiftRepository.deleteById(shift.getId());

        assertFalse(workShiftRepository.existsById(shift.getId()));
    }
}