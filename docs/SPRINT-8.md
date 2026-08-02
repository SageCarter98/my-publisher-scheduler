# Sprint 8 — Dashboards, Reports, Exports, and Audit Review

## Delivered

- Organization-scoped operational dashboard metrics.
- Schedule, assignment, attendance, user, and overview reports.
- Date-range report filtering.
- CSV, XLSX, and PDF report exports.
- Server-side authorization checks before every report and export.
- Audit-log filtering by action, entity type, outcome, actor, and date range.
- Controlled CSV audit export.
- Reporting and audit indexes.
- Protected React reporting workspace and audit review screen.

## Security notes

Reports are calculated from organization-scoped SQL queries. Export endpoints independently re-check authorization and do not trust client-side filtering.

## API additions

- `GET /api/v1/reports/dashboard`
- `GET /api/v1/reports?type={overview|schedules|assignments|attendance|users}`
- `GET /api/v1/reports/export?type=...&format={csv|xlsx|pdf}`
- `GET /api/v1/audit` with optional filters
- `GET /api/v1/audit/export` with optional filters
