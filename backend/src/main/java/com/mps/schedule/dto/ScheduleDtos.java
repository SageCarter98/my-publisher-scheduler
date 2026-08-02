package com.mps.schedule.dto;

import com.mps.schedule.model.RecurrenceFrequency;
import com.mps.schedule.model.ScheduleStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class ScheduleDtos {
    private ScheduleDtos() {}

    public enum EditScope { SINGLE, FUTURE, SERIES }

    public record RecurrenceRequest(
            @NotNull RecurrenceFrequency frequency,
            @Min(1) @Max(12) Integer interval,
            @Min(1) @Max(104) Integer count) {
        public int safeInterval() { return interval == null ? 1 : interval; }
        public int safeCount() { return frequency == RecurrenceFrequency.NONE ? 1 : (count == null ? 1 : count); }
    }

    public record CreateScheduleRequest(
            @NotBlank @Size(max = 200) String title,
            @Size(max = 2000) String description,
            @Size(max = 300) String location,
            @NotNull Instant startAt,
            @NotNull Instant endAt,
            @NotBlank @Size(max = 64) String timezone,
            @Valid @NotNull RecurrenceRequest recurrence) {}

    public record UpdateScheduleRequest(
            @NotBlank @Size(max = 200) String title,
            @Size(max = 2000) String description,
            @Size(max = 300) String location,
            @NotNull Instant startAt,
            @NotNull Instant endAt,
            @NotBlank @Size(max = 64) String timezone,
            @NotNull EditScope scope) {}

    public record CancelScheduleRequest(
            @NotBlank @Size(max = 500) String reason,
            @NotNull EditScope scope) {}

    public record ScheduleView(
            UUID id, UUID seriesId, int occurrenceNumber, String title, String description, String location,
            Instant startAt, Instant endAt, String timezone, ScheduleStatus status,
            RecurrenceFrequency recurrenceFrequency, int recurrenceInterval,
            Instant publishedAt, Instant cancelledAt, String cancellationReason, long version) {}

    public record ScheduleSeriesView(UUID seriesId, List<ScheduleView> occurrences) {}
}
