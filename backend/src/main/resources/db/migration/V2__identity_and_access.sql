ALTER TABLE app_user
    ADD COLUMN failed_login_attempts INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN locked_until TIMESTAMPTZ,
    ADD COLUMN last_login_at TIMESTAMPTZ,
    ADD COLUMN password_changed_at TIMESTAMPTZ;

CREATE TABLE role (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(500),
    system_role BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE permission (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(120) NOT NULL UNIQUE,
    description VARCHAR(500) NOT NULL
);

CREATE TABLE role_permission (
    role_id UUID NOT NULL REFERENCES role(id) ON DELETE CASCADE,
    permission_id UUID NOT NULL REFERENCES permission(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE user_role (
    user_id UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES role(id) ON DELETE RESTRICT,
    assigned_by UUID REFERENCES app_user(id),
    assigned_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE refresh_token (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    revoked_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_ip VARCHAR(64),
    user_agent VARCHAR(500)
);

CREATE INDEX ix_refresh_token_user ON refresh_token(user_id, expires_at DESC);
CREATE INDEX ix_user_locked_until ON app_user(locked_until) WHERE locked_until IS NOT NULL;

INSERT INTO role(code, name, description) VALUES
('SUPER_ADMIN', 'Super Administrator', 'Platform-level administration and security oversight.'),
('ORG_ADMIN', 'Organization Administrator', 'Organization, user, role, and configuration management.'),
('COORDINATOR', 'Coordinator', 'Scheduling, assignments, attendance, and communication operations.'),
('MEMBER', 'Member', 'Personal schedules, assignments, availability, and permitted content.')
ON CONFLICT (code) DO NOTHING;

INSERT INTO permission(code, description) VALUES
('organization.read', 'View organization information.'),
('organization.manage', 'Manage organization configuration.'),
('users.read', 'View organization users.'),
('users.manage', 'Create, update, archive, and restore users.'),
('roles.manage', 'Assign roles within permitted scope.'),
('schedules.read', 'View authorized schedules.'),
('schedules.manage', 'Create and maintain schedules.'),
('schedules.publish', 'Publish and cancel schedules.'),
('assignments.read', 'View authorized assignments.'),
('assignments.manage', 'Create and maintain assignments.'),
('availability.manage_self', 'Maintain personal availability.'),
('attendance.manage', 'Record and finalize attendance.'),
('announcements.manage', 'Create and publish announcements.'),
('documents.manage', 'Upload and maintain documents.'),
('reports.read', 'View authorized reports.'),
('audit.read', 'Review authorized audit events.')
ON CONFLICT (code) DO NOTHING;

INSERT INTO role_permission(role_id, permission_id)
SELECT r.id, p.id FROM role r CROSS JOIN permission p WHERE r.code = 'SUPER_ADMIN'
ON CONFLICT DO NOTHING;

INSERT INTO role_permission(role_id, permission_id)
SELECT r.id, p.id FROM role r JOIN permission p ON p.code IN (
    'organization.read','organization.manage','users.read','users.manage','roles.manage',
    'schedules.read','schedules.manage','schedules.publish','assignments.read','assignments.manage',
    'attendance.manage','announcements.manage','documents.manage','reports.read','audit.read'
) WHERE r.code = 'ORG_ADMIN'
ON CONFLICT DO NOTHING;

INSERT INTO role_permission(role_id, permission_id)
SELECT r.id, p.id FROM role r JOIN permission p ON p.code IN (
    'organization.read','users.read','schedules.read','schedules.manage','schedules.publish',
    'assignments.read','assignments.manage','availability.manage_self','attendance.manage',
    'announcements.manage','documents.manage','reports.read'
) WHERE r.code = 'COORDINATOR'
ON CONFLICT DO NOTHING;

INSERT INTO role_permission(role_id, permission_id)
SELECT r.id, p.id FROM role r JOIN permission p ON p.code IN (
    'organization.read','schedules.read','assignments.read','availability.manage_self','reports.read'
) WHERE r.code = 'MEMBER'
ON CONFLICT DO NOTHING;
