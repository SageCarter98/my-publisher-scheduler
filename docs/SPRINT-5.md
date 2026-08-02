# Sprint 5 — Assignments, Availability, and Conflict Detection

Implemented:
- Organization-scoped availability records and member self-service API.
- Assignment slots, assignees, status lifecycle, and optimistic locking.
- Eligibility validation for active organization members.
- Overlapping assignment and declared-unavailability conflict detection.
- Controlled warning overrides and audited reassignment.
- Publication-readiness checks integrated into schedule publication.
- PostgreSQL constraints and range indexes.

Primary endpoints:
- `GET/POST /api/v1/availability/me`
- `GET/POST /api/v1/assignments`
- `POST /api/v1/assignments/{id}/reassign`
- `POST /api/v1/assignments/schedule/{scheduleId}`
- `GET /api/v1/assignments/readiness/{scheduleId}`
