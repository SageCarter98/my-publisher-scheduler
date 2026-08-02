CREATE TABLE announcement (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organization(id) ON DELETE RESTRICT,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    audience_type VARCHAR(24) NOT NULL,
    audience_reference_id UUID,
    status VARCHAR(24) NOT NULL DEFAULT 'DRAFT',
    publish_at TIMESTAMPTZ,
    expires_at TIMESTAMPTZ,
    published_at TIMESTAMPTZ,
    created_by UUID NOT NULL REFERENCES app_user(id) ON DELETE RESTRICT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_announcement_audience CHECK (audience_type IN ('ORGANIZATION','DEPARTMENT','GROUP','ROLE','USER')),
    CONSTRAINT ck_announcement_status CHECK (status IN ('DRAFT','SCHEDULED','PUBLISHED','EXPIRED','ARCHIVED')),
    CONSTRAINT ck_announcement_window CHECK (expires_at IS NULL OR publish_at IS NULL OR expires_at > publish_at)
);
CREATE INDEX ix_announcement_org_status_publish ON announcement(organization_id, status, publish_at DESC);

CREATE TABLE notification (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organization(id) ON DELETE RESTRICT,
    recipient_id UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    notification_type VARCHAR(64) NOT NULL,
    title VARCHAR(200) NOT NULL,
    message VARCHAR(2000) NOT NULL,
    related_entity_type VARCHAR(64),
    related_entity_id UUID,
    read_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX ix_notification_recipient_unread ON notification(organization_id, recipient_id, read_at, created_at DESC);

CREATE TABLE notification_delivery (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    notification_id UUID NOT NULL REFERENCES notification(id) ON DELETE CASCADE,
    channel VARCHAR(24) NOT NULL,
    status VARCHAR(24) NOT NULL DEFAULT 'QUEUED',
    attempts INTEGER NOT NULL DEFAULT 0,
    next_attempt_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    last_attempt_at TIMESTAMPTZ,
    delivered_at TIMESTAMPTZ,
    failure_reason VARCHAR(1000),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_delivery_channel CHECK (channel IN ('IN_APP','EMAIL','PUSH')),
    CONSTRAINT ck_delivery_status CHECK (status IN ('QUEUED','SENT','DELIVERED','FAILED','RETRYING')),
    CONSTRAINT uq_notification_channel UNIQUE(notification_id, channel)
);
CREATE INDEX ix_notification_delivery_due ON notification_delivery(status, next_attempt_at);

INSERT INTO permission(code, description) VALUES
('notifications.read', 'View and manage personal notifications.'),
('announcements.read', 'View authorized announcements.')
ON CONFLICT (code) DO NOTHING;

INSERT INTO role_permission(role_id, permission_id)
SELECT r.id, p.id FROM role r CROSS JOIN permission p
WHERE r.code = 'SUPER_ADMIN' AND p.code IN ('notifications.read','announcements.read')
ON CONFLICT DO NOTHING;

INSERT INTO role_permission(role_id, permission_id)
SELECT r.id, p.id FROM role r JOIN permission p ON p.code IN ('notifications.read','announcements.read')
WHERE r.code IN ('ORG_ADMIN','COORDINATOR','MEMBER')
ON CONFLICT DO NOTHING;
