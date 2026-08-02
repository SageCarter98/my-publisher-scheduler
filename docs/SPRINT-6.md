# Sprint 6 — Announcements and Notifications

## Delivered
- Organization, department, group, role, and user announcement audiences.
- Draft, scheduled, published, expired, and archived announcement states.
- In-app notifications with unread counts and read-state management.
- Notification delivery outbox with channel status, retry count, exponential backoff, and failure recording.
- Schedule publication and cancellation alerts for affected assignees.
- Announcement publication notifications and external delivery queueing.
- Protected React announcement workspace and notification center.

## External providers
Email and push deliveries are deliberately adapter-ready but unconfigured. The worker records retries and final failure rather than pretending delivery succeeded. Provider adapters are a deployment decision.

## Primary endpoints
- `GET /api/v1/notifications`
- `GET /api/v1/notifications/summary`
- `PUT /api/v1/notifications/{id}/read`
- `GET /api/v1/announcements`
- `POST /api/v1/announcements`
- `POST /api/v1/announcements/{id}/publish`
- `POST /api/v1/announcements/{id}/archive`
