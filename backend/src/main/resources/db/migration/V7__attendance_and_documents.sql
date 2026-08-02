CREATE TABLE attendance_register (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organization(id) ON DELETE RESTRICT,
    schedule_id UUID NOT NULL REFERENCES schedule_event(id) ON DELETE RESTRICT,
    status VARCHAR(24) NOT NULL DEFAULT 'DRAFT',
    finalized_at TIMESTAMPTZ,
    finalized_by UUID REFERENCES app_user(id) ON DELETE RESTRICT,
    created_by UUID NOT NULL REFERENCES app_user(id) ON DELETE RESTRICT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_attendance_register_schedule UNIQUE(organization_id, schedule_id),
    CONSTRAINT ck_attendance_register_status CHECK (status IN ('DRAFT','FINALIZED'))
);

CREATE TABLE attendance_entry (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    register_id UUID NOT NULL REFERENCES attendance_register(id) ON DELETE CASCADE,
    member_id UUID NOT NULL REFERENCES app_user(id) ON DELETE RESTRICT,
    attendance_status VARCHAR(24) NOT NULL,
    remarks VARCHAR(500),
    corrected_at TIMESTAMPTZ,
    corrected_by UUID REFERENCES app_user(id) ON DELETE RESTRICT,
    correction_reason VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_attendance_entry_member UNIQUE(register_id, member_id),
    CONSTRAINT ck_attendance_status CHECK (attendance_status IN ('PRESENT','ABSENT','EXCUSED','LATE'))
);
CREATE INDEX ix_attendance_entry_register ON attendance_entry(register_id, attendance_status);

CREATE TABLE document_record (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organization(id) ON DELETE RESTRICT,
    title VARCHAR(200) NOT NULL,
    category VARCHAR(100),
    audience_type VARCHAR(24) NOT NULL,
    audience_reference_id UUID,
    status VARCHAR(24) NOT NULL DEFAULT 'ACTIVE',
    current_version INTEGER NOT NULL DEFAULT 1,
    uploaded_by UUID NOT NULL REFERENCES app_user(id) ON DELETE RESTRICT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    archived_at TIMESTAMPTZ,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_document_audience CHECK (audience_type IN ('ORGANIZATION','DEPARTMENT','GROUP','ROLE','USER')),
    CONSTRAINT ck_document_status CHECK (status IN ('ACTIVE','ARCHIVED'))
);
CREATE INDEX ix_document_record_org_status ON document_record(organization_id, status, created_at DESC);

CREATE TABLE document_version (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    document_id UUID NOT NULL REFERENCES document_record(id) ON DELETE CASCADE,
    version_number INTEGER NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    stored_filename VARCHAR(255) NOT NULL,
    content_type VARCHAR(150) NOT NULL,
    size_bytes BIGINT NOT NULL,
    sha256 VARCHAR(64) NOT NULL,
    storage_path VARCHAR(1000) NOT NULL,
    version_notes VARCHAR(500),
    uploaded_by UUID NOT NULL REFERENCES app_user(id) ON DELETE RESTRICT,
    uploaded_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_document_version UNIQUE(document_id, version_number),
    CONSTRAINT ck_document_size CHECK (size_bytes > 0)
);
CREATE INDEX ix_document_version_document ON document_version(document_id, version_number DESC);

INSERT INTO permission(code, description) VALUES
('attendance.read', 'View attendance registers.'),
('attendance.manage', 'Create and update attendance registers.'),
('attendance.finalize', 'Finalize attendance registers.'),
('attendance.correct', 'Correct finalized attendance records.'),
('documents.read', 'View and download authorized documents.'),
('documents.manage', 'Upload, version, and archive documents.')
ON CONFLICT (code) DO NOTHING;

INSERT INTO role_permission(role_id, permission_id)
SELECT r.id, p.id FROM role r CROSS JOIN permission p
WHERE r.code = 'SUPER_ADMIN' AND p.code IN ('attendance.read','attendance.manage','attendance.finalize','attendance.correct','documents.read','documents.manage')
ON CONFLICT DO NOTHING;

INSERT INTO role_permission(role_id, permission_id)
SELECT r.id, p.id FROM role r JOIN permission p ON p.code IN ('attendance.read','attendance.manage','attendance.finalize','attendance.correct','documents.read','documents.manage')
WHERE r.code = 'ORG_ADMIN'
ON CONFLICT DO NOTHING;

INSERT INTO role_permission(role_id, permission_id)
SELECT r.id, p.id FROM role r JOIN permission p ON p.code IN ('attendance.read','attendance.manage','attendance.finalize','documents.read','documents.manage')
WHERE r.code = 'COORDINATOR'
ON CONFLICT DO NOTHING;

INSERT INTO role_permission(role_id, permission_id)
SELECT r.id, p.id FROM role r JOIN permission p ON p.code IN ('documents.read')
WHERE r.code = 'MEMBER'
ON CONFLICT DO NOTHING;
