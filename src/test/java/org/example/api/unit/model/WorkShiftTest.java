package org.example.api.unit.model;

import io.hypersistence.utils.hibernate.type.range.Range;
import org.example.api.model.Waiter;
import org.example.api.model.WorkShift;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class WorkShiftTest {

    @Nested
    @DisplayName("Builder and Constructor Tests")
    class BuilderTests {

        @Test
        @DisplayName("Should create WorkShift using builder")
        void shouldCreateWorkShiftUsingBuilder() {
            Waiter waiter = Waiter.builder().id(1L).build();
            LocalDateTime start = LocalDateTime.of(2024, 1, 15, 8, 0);
            LocalDateTime end = LocalDateTime.of(2024, 1, 15, 16, 0);
            Range<LocalDateTime> shiftPeriod = Range.closedOpen(start, end);

            WorkShift workShift = WorkShift.builder()
                    .id(1L)
                    .waiter(waiter)
                    .shiftPeriod(shiftPeriod)
                    .build();

            assertEquals(1L, workShift.getId());
            assertEquals(waiter, workShift.getWaiter());
            assertEquals(shiftPeriod, workShift.getShiftPeriod());
        }
    }

    @Nested
    @DisplayName("Shift Period Tests")
    class ShiftPeriodTests {

        @Test
        @DisplayName("Should handle closedOpen range")
        void shouldHandleClosedOpenRange() {
            LocalDateTime start = LocalDateTime.of(2024, 1, 15, 10, 0);
            LocalDateTime end = LocalDateTime.of(2024, 1, 15, 18, 0);
            Range<LocalDateTime> period = Range.closedOpen(start, end);

            WorkShift workShift = WorkShift.builder()
                    .shiftPeriod(period)
                    .build();

            assertEquals(start, workShift.getShiftPeriod().lower());
            assertEquals(end, workShift.getShiftPeriod().upper());
        }
    }

    @Nested
    @DisplayName("Equals and HashCode Tests")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("Should be equal when ids are the same")
        void shouldBeEqualWhenIdsAreSame() {
            WorkShift ws1 = WorkShift.builder().id(1L).build();
            WorkShift ws2 = WorkShift.builder().id(1L).build();

            assertEquals(ws1, ws2);
        }

        @Test
        @DisplayName("Should not be equal when ids are different")
        void shouldNotBeEqualWhenIdsAreDifferent() {
            WorkShift ws1 = WorkShift.builder().id(1L).build();
            WorkShift ws2 = WorkShift.builder().id(2L).build();

            assertNotEquals(ws1, ws2);
        }
    }
}