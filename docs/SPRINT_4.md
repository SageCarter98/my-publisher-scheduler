# Sprint 4 — Scheduling and Calendar

## Delivered

- Organization-scoped schedule storage and APIs.
- Draft, Published, Completed, Cancelled, and Archived lifecycle model.
- Calendar range queries with bounded date windows.
- Daily, weekly, and monthly recurrence generation.
- Series identifiers and occurrence numbering.
- Single, future-occurrence, and full-series editing scopes.
- Publication, cancellation, and completion controls.
- Optimistic locking for concurrent edits.
- Audit events for create, update, publish, cancel, and complete actions.
- PostgreSQL constraints and indexes for schedule ranges and series.
- Recurrence planner unit tests.

## API

- `GET /api/v1/schedules?from=<ISO>&to=<ISO>`
- `GET /api/v1/schedules/{id}`
- `POST /api/v1/schedules`
- `PUT /api/v1/schedules/{id}`
- `POST /api/v1/schedules/{id}/publish?scope=SINGLE|FUTURE|SERIES`
- `POST /api/v1/schedules/{id}/cancel`
- `POST /api/v1/schedules/{id}/complete`

## Next

Sprint 5 adds assignment slots, member availability, eligibility checks, conflict detection, reassignment, and schedule publication readiness checks.
