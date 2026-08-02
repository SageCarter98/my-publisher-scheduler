# My Publisher Scheduler 1.0.0-rc1

## Included
Authentication, RBAC, organization management, users, departments, groups, schedules, recurrence, assignments, availability, conflict detection, announcements, notifications, attendance, documents, dashboards, reports, audit review, settings, backups, health checks and release tooling.

## Release status
Release candidate. It is not production-approved until CI, staging security, performance, recovery and UAT gates pass.

## Known integration requirement
Email and mobile push delivery require approved provider adapters and credentials. In-app notifications are implemented; external delivery records remain transparent and retryable until providers are configured.

## Operational requirement
Backup requests are queued by the application and executed by the restricted external backup runner.
