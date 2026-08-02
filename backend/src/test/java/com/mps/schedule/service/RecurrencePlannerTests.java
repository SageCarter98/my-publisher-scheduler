package com.mps.schedule.service;

import com.mps.schedule.model.RecurrenceFrequency;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.assertj.core.api.Assertions.assertThat;

class RecurrencePlannerTests {
    private final RecurrencePlanner planner = new RecurrencePlanner();

    @Test
    void createsWeeklyOccurrencesWithStableDuration() {
        Instant start = Instant.parse("2026-08-03T18:00:00Z");
        Instant end = Instant.parse("2026-08-03T20:00:00Z");
        var windows = planner.plan(start, end, "Africa/Accra", RecurrenceFrequency.WEEKLY, 1, 3);
        assertThat(windows).hasSize(3);
        assertThat(windows.get(1).startAt()).isEqualTo(Instant.parse("2026-08-10T18:00:00Z"));
        assertThat(windows.get(2).endAt()).isEqualTo(Instant.parse("2026-08-17T20:00:00Z"));
    }

    @Test
    void noneCreatesOneOccurrence() {
        var windows = planner.plan(Instant.parse("2026-08-03T18:00:00Z"),
                Instant.parse("2026-08-03T19:00:00Z"), "UTC", RecurrenceFrequency.NONE, 1, 99);
        assertThat(windows).hasSize(1);
    }
}
