package com.mps.schedule.model;

import com.mps.organization.model.Organization;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "schedule_event")
public class ScheduleEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(name = "series_id")
    private UUID seriesId;

    @Column(name = "occurrence_number", nullable = false)
    private int occurrenceNumber;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(length = 300)
    private String location;

    @Column(name = "start_at", nullable = false)
    private Instant startAt;

    @Column(name = "end_at", nullable = false)
    private Instant endAt;

    @Column(nullable = false, length = 64)
    private String timezone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ScheduleStatus status = ScheduleStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(name = "recurrence_frequency", nullable = false, length = 16)
    private RecurrenceFrequency recurrenceFrequency = RecurrenceFrequency.NONE;

    @Column(name = "recurrence_interval", nullable = false)
    private int recurrenceInterval = 1;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @Version
    private long version;

    protected ScheduleEvent() {}

    public ScheduleEvent(Organization organization, UUID seriesId, int occurrenceNumber, String title,
                         String description, String location, Instant startAt, Instant endAt, String timezone,
                         RecurrenceFrequency recurrenceFrequency, int recurrenceInterval, UUID createdBy) {
        this.organization = organization;
        this.seriesId = seriesId;
        this.occurrenceNumber = occurrenceNumber;
        this.title = title.trim();
        this.description = normalize(description);
        this.location = normalize(location);
        this.startAt = startAt;
        this.endAt = endAt;
        this.timezone = timezone;
        this.recurrenceFrequency = recurrenceFrequency;
        this.recurrenceInterval = recurrenceInterval;
        this.createdBy = createdBy;
    }

    public void update(String title, String description, String location, Instant startAt, Instant endAt, String timezone) {
        requireEditable();
        this.title = title.trim();
        this.description = normalize(description);
        this.location = normalize(location);
        this.startAt = startAt;
        this.endAt = endAt;
        this.timezone = timezone;
        this.updatedAt = Instant.now();
    }

    public void publish() {
        if (status != ScheduleStatus.DRAFT) throw new IllegalStateException("Only draft schedules can be published.");
        status = ScheduleStatus.PUBLISHED;
        publishedAt = Instant.now();
        updatedAt = publishedAt;
    }

    public void cancel(String reason) {
        if (status != ScheduleStatus.DRAFT && status != ScheduleStatus.PUBLISHED) {
            throw new IllegalStateException("Only draft or published schedules can be cancelled.");
        }
        status = ScheduleStatus.CANCELLED;
        cancellationReason = reason.trim();
        cancelledAt = Instant.now();
        updatedAt = cancelledAt;
    }

    public void complete() {
        if (status != ScheduleStatus.PUBLISHED) throw new IllegalStateException("Only published schedules can be completed.");
        status = ScheduleStatus.COMPLETED;
        updatedAt = Instant.now();
    }

    private void requireEditable() {
        if (status == ScheduleStatus.COMPLETED || status == ScheduleStatus.CANCELLED || status == ScheduleStatus.ARCHIVED) {
            throw new IllegalStateException("This schedule can no longer be edited.");
        }
    }

    private static String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    public UUID getId() { return id; }
    public Organization getOrganization() { return organization; }
    public UUID getSeriesId() { return seriesId; }
    public int getOccurrenceNumber() { return occurrenceNumber; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public Instant getStartAt() { return startAt; }
    public Instant getEndAt() { return endAt; }
    public String getTimezone() { return timezone; }
    public ScheduleStatus getStatus() { return status; }
    public RecurrenceFrequency getRecurrenceFrequency() { return recurrenceFrequency; }
    public int getRecurrenceInterval() { return recurrenceInterval; }
    public UUID getCreatedBy() { return createdBy; }
    public Instant getPublishedAt() { return publishedAt; }
    public Instant getCancelledAt() { return cancelledAt; }
    public String getCancellationReason() { return cancellationReason; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public long getVersion() { return version; }
}
