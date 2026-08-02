package com.mps.schedule.repository;

import com.mps.schedule.model.ScheduleEvent;
import com.mps.schedule.model.ScheduleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScheduleEventRepository extends JpaRepository<ScheduleEvent, UUID> {
    Optional<ScheduleEvent> findByIdAndOrganizationId(UUID id, UUID organizationId);
    List<ScheduleEvent> findAllByOrganizationIdAndStartAtLessThanAndEndAtGreaterThanOrderByStartAtAsc(
            UUID organizationId, Instant rangeEnd, Instant rangeStart);
    List<ScheduleEvent> findAllByOrganizationIdAndSeriesIdOrderByOccurrenceNumberAsc(UUID organizationId, UUID seriesId);
    List<ScheduleEvent> findAllByOrganizationIdAndSeriesIdAndOccurrenceNumberGreaterThanEqualOrderByOccurrenceNumberAsc(
            UUID organizationId, UUID seriesId, int occurrenceNumber);
    long countByOrganizationIdAndStartAtLessThanAndEndAtGreaterThanAndStatusNot(
            UUID organizationId, Instant endAt, Instant startAt, ScheduleStatus status);
}
