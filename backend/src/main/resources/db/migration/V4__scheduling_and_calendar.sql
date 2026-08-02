CREATE TABLE schedule_event (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organization(id) ON DELETE RESTRICT,
    series_id UUID,
    occurrence_number INTEGER NOT NULL DEFAULT 1,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(2000),
    location VARCHAR(300),
    start_at TIMESTAMPTZ NOT NULL,
    end_at TIMESTAMPTZ NOT NULL,
    timezone VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'DRAFT',
    recurrence_frequency VARCHAR(16) NOT NULL DEFAULT 'NONE',
    recurrence_interval INTEGER NOT NULL DEFAULT 1,
    created_by UUID NOT NULL REFERENCES app_user(id) ON DELETE RESTRICT,
    published_at TIMESTAMPTZ,
    cancelled_at TIMESTAMPTZ,
    cancellation_reason VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_schedule_window CHECK (end_at > start_at),
    CONSTRAINT ck_schedule_status CHECK (status IN ('DRAFT','PUBLISHED','COMPLETED','CANCELLED','ARCHIVED')),
    CONSTRAINT ck_schedule_recurrence CHECK (recurrence_frequency IN ('NONE','DAILY','WEEKLY','MONTHLY')),
    CONSTRAINT ck_schedule_recurrence_interval CHECK (recurrence_interval BETWEEN 1 AND 12),
    CONSTRAINT ck_schedule_occurrence CHECK (occurrence_number >= 1),
    CONSTRAINT uq_schedule_series_occurrence UNIQUE (organization_id, series_id, occurrence_number)
);

CREATE INDEX ix_schedule_org_start ON schedule_event(organization_id, start_at);
CREATE INDEX ix_schedule_org_range ON schedule_event(organization_id, start_at, end_at);
CREATE INDEX ix_schedule_org_status ON schedule_event(organization_id, status, start_at);
CREATE INDEX ix_schedule_series ON schedule_event(organization_id, series_id, occurrence_number)
    WHERE series_id IS NOT NULL;
