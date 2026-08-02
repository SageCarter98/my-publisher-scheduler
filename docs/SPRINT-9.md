# Sprint 9 — Settings and Operational Readiness

## Delivered
- Typed organization settings with validation and audit history.
- Backup request records and an external operational runner contract.
- Database/document backup and integrity-verification scripts.
- Protected operational health endpoint covering database and document storage.
- Operational alert model and acknowledgement workflow.
- Readiness/liveness actuator configuration and deployment smoke checks.
- Database indexes for settings, backups, and alerts.

## Important operational boundary
The application queues and audits backup requests; it does not execute `pg_dump` inside the web process. The supplied `ops/backup-runner.sh` is intended for a protected scheduler, container job, or platform-native cron service with least-privilege credentials.

## New endpoints
- `GET /api/v1/settings`
- `PUT /api/v1/settings`
- `GET /api/v1/operations/health`
- `GET /api/v1/operations/backups`
- `POST /api/v1/operations/backups?type=FULL`
- `GET /api/v1/operations/alerts`
- `POST /api/v1/operations/alerts/{id}/acknowledge`
