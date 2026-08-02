package com.mps.schedule.service;

import com.mps.audit.service.AuditService;
import com.mps.assignment.service.AssignmentService;
import com.mps.assignment.repository.AssignmentRepository;
import com.mps.notification.service.NotificationService;
import com.mps.auth.model.AppUser;
import com.mps.auth.security.UserPrincipal;
import com.mps.organization.model.Organization;
import com.mps.organization.repository.OrganizationRepository;
import com.mps.schedule.dto.ScheduleDtos.*;
import com.mps.schedule.model.*;
import com.mps.schedule.repository.ScheduleEventRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.*;
import java.util.*;

@Service
public class ScheduleService {
    private final ScheduleEventRepository schedules;
    private final OrganizationRepository organizations;
    private final RecurrencePlanner recurrencePlanner;
    private final AuditService audit;
    private final AssignmentService assignments;
    private final AssignmentRepository assignmentRepository;
    private final NotificationService notifications;

    public ScheduleService(ScheduleEventRepository schedules, OrganizationRepository organizations,
                           RecurrencePlanner recurrencePlanner, AuditService audit, AssignmentService assignments,
                           AssignmentRepository assignmentRepository, NotificationService notifications) {
        this.schedules = schedules;
        this.organizations = organizations;
        this.recurrencePlanner = recurrencePlanner;
        this.audit = audit;
        this.assignments = assignments;
        this.assignmentRepository = assignmentRepository;
        this.notifications = notifications;
    }

    @Transactional(readOnly = true)
    public List<ScheduleView> calendar(UserPrincipal principal, Instant from, Instant to) {
        validateRange(from, to);
        return schedules.findAllByOrganizationIdAndStartAtLessThanAndEndAtGreaterThanOrderByStartAtAsc(
                principal.organizationId(), to, from).stream().map(this::view).toList();
    }

    @Transactional(readOnly = true)
    public ScheduleView get(UserPrincipal principal, UUID id) {
        return view(schedule(principal, id));
    }

    @Transactional
    public ScheduleSeriesView create(UserPrincipal principal, CreateScheduleRequest request) {
        validateWindow(request.startAt(), request.endAt(), request.timezone());
        RecurrenceRequest recurrence = request.recurrence();
        List<RecurrencePlanner.Window> windows;
        try {
            windows = recurrencePlanner.plan(request.startAt(), request.endAt(), request.timezone(),
                    recurrence.frequency(), recurrence.safeInterval(), recurrence.safeCount());
        } catch (DateTimeException | IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
        Organization organization = organization(principal);
        UUID seriesId = windows.size() > 1 ? UUID.randomUUID() : null;
        List<ScheduleEvent> created = new ArrayList<>();
        for (int index = 0; index < windows.size(); index++) {
            RecurrencePlanner.Window window = windows.get(index);
            created.add(new ScheduleEvent(organization, seriesId, index + 1, request.title(), request.description(),
                    request.location(), window.startAt(), window.endAt(), request.timezone(),
                    recurrence.frequency(), recurrence.safeInterval(), principal.userId()));
        }
        schedules.saveAll(created);
        audit.record(organization, principal.userId(), "SCHEDULE_CREATED", "Schedule",
                created.getFirst().getId(), "SUCCESS", Map.of("occurrences", created.size(), "seriesId", String.valueOf(seriesId)));
        return new ScheduleSeriesView(seriesId, created.stream().map(this::view).toList());
    }

    @Transactional
    public List<ScheduleView> update(UserPrincipal principal, UUID id, UpdateScheduleRequest request) {
        validateWindow(request.startAt(), request.endAt(), request.timezone());
        ScheduleEvent selected = schedule(principal, id);
        List<ScheduleEvent> targets = targets(principal, selected, request.scope());
        Duration offset = Duration.between(selected.getStartAt(), request.startAt());
        Duration duration = Duration.between(request.startAt(), request.endAt());
        for (ScheduleEvent target : targets) {
            Instant start = request.scope() == EditScope.SINGLE ? request.startAt() : target.getStartAt().plus(offset);
            target.update(request.title(), request.description(), request.location(), start, start.plus(duration), request.timezone());
        }
        audit.record(selected.getOrganization(), principal.userId(), "SCHEDULE_UPDATED", "Schedule", id,
                "SUCCESS", Map.of("scope", request.scope().name(), "affected", targets.size()));
        return targets.stream().map(this::view).toList();
    }

    @Transactional
    public List<ScheduleView> publish(UserPrincipal principal, UUID id, EditScope scope) {
        ScheduleEvent selected = schedule(principal, id);
        List<ScheduleEvent> targets = targets(principal, selected, scope);
        for (ScheduleEvent target : targets) {
            var readiness = assignments.readiness(principal, target.getId());
            if (!readiness.ready()) throw new ResponseStatusException(HttpStatus.CONFLICT, String.join("; ", readiness.blockers()));
            assignments.scheduleDraftAssignments(principal, target.getId());
        }
        try {
            targets.forEach(ScheduleEvent::publish);
        } catch (IllegalStateException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, exception.getMessage());
        }
        for (ScheduleEvent target : targets) {
            List<AppUser> recipients = assignmentRepository.findAllByOrganizationIdAndScheduleIdOrderByAssignmentTypeAsc(
                    principal.organizationId(), target.getId()).stream().map(a -> a.getAssignee()).filter(Objects::nonNull).distinct().toList();
            notifications.notifyUsers(target.getOrganization(), recipients, "SCHEDULE_PUBLISHED", target.getTitle(),
                    "A schedule containing your assignment has been published.", "Schedule", target.getId(), true);
        }
        audit.record(selected.getOrganization(), principal.userId(), "SCHEDULE_PUBLISHED", "Schedule", id,
                "SUCCESS", Map.of("scope", scope.name(), "affected", targets.size()));
        return targets.stream().map(this::view).toList();
    }

    @Transactional
    public List<ScheduleView> cancel(UserPrincipal principal, UUID id, CancelScheduleRequest request) {
        ScheduleEvent selected = schedule(principal, id);
        List<ScheduleEvent> targets = targets(principal, selected, request.scope());
        try {
            targets.forEach(target -> target.cancel(request.reason()));
        } catch (IllegalStateException exception) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, exception.getMessage());
        }
        for (ScheduleEvent target : targets) {
            List<AppUser> recipients = assignmentRepository.findAllByOrganizationIdAndScheduleIdOrderByAssignmentTypeAsc(
                    principal.organizationId(), target.getId()).stream().map(a -> a.getAssignee()).filter(Objects::nonNull).distinct().toList();
            notifications.notifyUsers(target.getOrganization(), recipients, "SCHEDULE_CANCELLED", target.getTitle(),
                    "This schedule has been cancelled: " + request.reason(), "Schedule", target.getId(), true);
        }
        audit.record(selected.getOrganization(), principal.userId(), "SCHEDULE_CANCELLED", "Schedule", id,
                "SUCCESS", Map.of("scope", request.scope().name(), "affected", targets.size(), "reason", request.reason()));
        return targets.stream().map(this::view).toList();
    }

    @Transactional
    public ScheduleView complete(UserPrincipal principal, UUID id) {
        ScheduleEvent selected = schedule(principal, id);
        try { selected.complete(); }
        catch (IllegalStateException exception) { throw new ResponseStatusException(HttpStatus.CONFLICT, exception.getMessage()); }
        audit.record(selected.getOrganization(), principal.userId(), "SCHEDULE_COMPLETED", "Schedule", id,
                "SUCCESS", Map.of());
        return view(selected);
    }

    private List<ScheduleEvent> targets(UserPrincipal principal, ScheduleEvent selected, EditScope scope) {
        if (scope == EditScope.SINGLE || selected.getSeriesId() == null) return List.of(selected);
        if (scope == EditScope.SERIES) {
            return schedules.findAllByOrganizationIdAndSeriesIdOrderByOccurrenceNumberAsc(
                    principal.organizationId(), selected.getSeriesId());
        }
        return schedules.findAllByOrganizationIdAndSeriesIdAndOccurrenceNumberGreaterThanEqualOrderByOccurrenceNumberAsc(
                principal.organizationId(), selected.getSeriesId(), selected.getOccurrenceNumber());
    }

    private void validateRange(Instant from, Instant to) {
        if (from == null || to == null || !to.isAfter(from)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A valid calendar range is required.");
        }
        if (Duration.between(from, to).toDays() > 370) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Calendar range cannot exceed 370 days.");
        }
    }

    private void validateWindow(Instant start, Instant end, String timezone) {
        if (start == null || end == null || !end.isAfter(start)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End time must be after start time.");
        }
        try { ZoneId.of(timezone); }
        catch (DateTimeException exception) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid timezone."); }
    }

    private ScheduleEvent schedule(UserPrincipal principal, UUID id) {
        return schedules.findByIdAndOrganizationId(id, principal.organizationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found."));
    }

    private Organization organization(UserPrincipal principal) {
        return organizations.findById(principal.organizationId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Organization not found."));
    }

    private ScheduleView view(ScheduleEvent event) {
        return new ScheduleView(event.getId(), event.getSeriesId(), event.getOccurrenceNumber(), event.getTitle(),
                event.getDescription(), event.getLocation(), event.getStartAt(), event.getEndAt(), event.getTimezone(),
                event.getStatus(), event.getRecurrenceFrequency(), event.getRecurrenceInterval(), event.getPublishedAt(),
                event.getCancelledAt(), event.getCancellationReason(), event.getVersion());
    }
}
