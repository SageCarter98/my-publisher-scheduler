CREATE TABLE availability_entry (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organization(id) ON DELETE RESTRICT,
    user_id UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    start_at TIMESTAMPTZ NOT NULL,
    end_at TIMESTAMPTZ NOT NULL,
    status VARCHAR(20) NOT NULL,
    notes VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_availability_window CHECK (end_at > start_at),
    CONSTRAINT ck_availability_status CHECK (status IN ('AVAILABLE','UNAVAILABLE'))
);
CREATE INDEX ix_availability_user_range ON availability_entry(organization_id, user_id, start_at, end_at);

CREATE TABLE assignment (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organization(id) ON DELETE RESTRICT,
    schedule_id UUID NOT NULL REFERENCES schedule_event(id) ON DELETE RESTRICT,
    assignee_id UUID REFERENCES app_user(id) ON DELETE RESTRICT,
    assignment_type VARCHAR(120) NOT NULL,
    title VARCHAR(200) NOT NULL,
    notes VARCHAR(1000),
    status VARCHAR(24) NOT NULL DEFAULT 'DRAFT',
    override_reason VARCHAR(500),
    reassigned_from UUID REFERENCES app_user(id) ON DELETE RESTRICT,
    created_by UUID NOT NULL REFERENCES app_user(id) ON DELETE RESTRICT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_assignment_status CHECK (status IN ('DRAFT','SCHEDULED','CONFIRMED','COMPLETED','CANCELLED','ARCHIVED'))
);
CREATE INDEX ix_assignment_schedule ON assignment(organization_id, schedule_id, status);
CREATE INDEX ix_assignment_assignee ON assignment(organization_id, assignee_id, status);
CREATE UNIQUE INDEX uq_active_assignment_slot ON assignment(schedule_id, assignment_type)
WHERE status NOT IN ('CANCELLED','ARCHIVED');
