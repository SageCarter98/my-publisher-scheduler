CREATE TABLE department (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organization(id) ON DELETE RESTRICT,
    name VARCHAR(160) NOT NULL,
    description VARCHAR(500),
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    archived_at TIMESTAMPTZ,
    CONSTRAINT uq_department_org_name UNIQUE (organization_id, name),
    CONSTRAINT ck_department_status CHECK (status IN ('ACTIVE','ARCHIVED'))
);

CREATE TABLE member_group (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organization(id) ON DELETE RESTRICT,
    department_id UUID REFERENCES department(id) ON DELETE RESTRICT,
    name VARCHAR(160) NOT NULL,
    description VARCHAR(500),
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    archived_at TIMESTAMPTZ,
    CONSTRAINT uq_group_org_name UNIQUE (organization_id, name),
    CONSTRAINT ck_group_status CHECK (status IN ('ACTIVE','ARCHIVED'))
);

ALTER TABLE app_user
    ADD COLUMN department_id UUID REFERENCES department(id) ON DELETE SET NULL,
    ADD COLUMN group_id UUID REFERENCES member_group(id) ON DELETE SET NULL;

CREATE INDEX ix_department_org_status ON department(organization_id, status, name);
CREATE INDEX ix_group_org_status ON member_group(organization_id, status, name);
CREATE INDEX ix_user_department ON app_user(department_id) WHERE department_id IS NOT NULL;
CREATE INDEX ix_user_group ON app_user(group_id) WHERE group_id IS NOT NULL;
