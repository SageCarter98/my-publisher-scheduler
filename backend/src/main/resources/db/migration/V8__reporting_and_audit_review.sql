INSERT INTO permission(code, description) VALUES
('reports.read', 'View operational reports and dashboards.'),
('reports.export', 'Export authorized operational reports.')
ON CONFLICT (code) DO NOTHING;

INSERT INTO role_permission(role_id, permission_id)
SELECT r.id,p.id FROM role r JOIN permission p ON p.code IN ('reports.read','reports.export')
WHERE r.code IN ('SUPER_ADMIN','ORG_ADMIN','COORDINATOR')
ON CONFLICT DO NOTHING;

INSERT INTO role_permission(role_id, permission_id)
SELECT r.id,p.id FROM role r JOIN permission p ON p.code='reports.read'
WHERE r.code='MEMBER'
ON CONFLICT DO NOTHING;

CREATE INDEX IF NOT EXISTS ix_audit_org_occurred ON audit_log(organization_id, occurred_at DESC);
CREATE INDEX IF NOT EXISTS ix_audit_org_action ON audit_log(organization_id, action, occurred_at DESC);
CREATE INDEX IF NOT EXISTS ix_attendance_register_org_status ON attendance_register(organization_id, status, created_at DESC);
