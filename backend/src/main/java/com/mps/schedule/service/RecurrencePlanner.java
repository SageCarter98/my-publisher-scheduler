package com.mps.schedule.service;

import com.mps.schedule.model.RecurrenceFrequency;
import org.springframework.stereotype.Component;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class RecurrencePlanner {
    public List<Window> plan(Instant startAt, Instant endAt, String timezone,
                             RecurrenceFrequency frequency, int interval, int count) {
        if (!endAt.isAfter(startAt)) throw new IllegalArgumentException("End time must be after start time.");
        ZoneId zone = ZoneId.of(timezone);
        if (frequency == RecurrenceFrequency.NONE) return List.of(new Window(startAt, endAt));
        if (count < 1 || count > 104) throw new IllegalArgumentException("Recurrence count must be between 1 and 104.");
        if (interval < 1 || interval > 12) throw new IllegalArgumentException("Recurrence interval must be between 1 and 12.");

        ZonedDateTime start = startAt.atZone(zone);
        Duration duration = Duration.between(startAt, endAt);
        List<Window> result = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            ZonedDateTime occurrenceStart = switch (frequency) {
                case DAILY -> start.plusDays((long) i * interval);
                case WEEKLY -> start.plusWeeks((long) i * interval);
                case MONTHLY -> start.plusMonths((long) i * interval);
                case NONE -> start;
            };
            Instant occurrenceStartInstant = occurrenceStart.toInstant();
            result.add(new Window(occurrenceStartInstant, occurrenceStartInstant.plus(duration)));
        }
        return List.copyOf(result);
    }

    public record Window(Instant startAt, Instant endAt) {}
}
