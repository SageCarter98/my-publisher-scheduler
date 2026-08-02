CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE organization (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    timezone VARCHAR(64) NOT NULL DEFAULT 'UTC',
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    archived_at TIMESTAMPTZ,
    CONSTRAINT uq_organization_name UNIQUE (name),
    CONSTRAINT ck_organization_status CHECK (status IN ('ACTIVE','SUSPENDED','ARCHIVED'))
);

CREATE TABLE app_user (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organization(id),
    email VARCHAR(320) NOT NULL,
    password_hash VARCHAR(255),
    first_name VARCHAR(120) NOT NULL,
    last_name VARCHAR(120) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING_ACTIVATION',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    archived_at TIMESTAMPTZ,
    CONSTRAINT uq_user_org_email UNIQUE (organization_id, email),
    CONSTRAINT ck_user_status CHECK (status IN ('PENDING_ACTIVATION','ACTIVE','LOCKED','SUSPENDED','ARCHIVED'))
);

CREATE TABLE audit_log (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID REFERENCES organization(id),
    actor_user_id UUID REFERENCES app_user(id),
    action VARCHAR(120) NOT NULL,
    entity_type VARCHAR(120) NOT NULL,
    entity_id UUID,
    outcome VARCHAR(32) NOT NULL,
    details JSONB NOT NULL DEFAULT '{}'::jsonb,
    occurred_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_audit_outcome CHECK (outcome IN ('SUCCESS','FAILURE','DENIED'))
);

CREATE INDEX ix_user_org_status ON app_user(organization_id, status);
CREATE INDEX ix_audit_org_time ON audit_log(organization_id, occurred_at DESC);
