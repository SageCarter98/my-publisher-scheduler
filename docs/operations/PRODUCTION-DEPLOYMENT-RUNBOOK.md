# MPS Production Deployment Runbook

## Preconditions
- CI backend, web, container, security and migration jobs pass.
- Staging UAT is approved.
- Production secrets are stored outside source control.
- Database and document backups are verified.
- Rollback owner and incident contacts are assigned.

## Deployment
1. Record the current application and database versions.
2. Run `ops/release/preflight.sh`.
3. Create a full backup using the approved backup runner.
4. Pull or build immutable release images.
5. Apply Flyway migrations through the backend startup process.
6. Start production services with `docker compose -f docker-compose.prod.yml up -d`.
7. Run `ops/release/post-deploy-verify.sh`.
8. Review logs, health probes, alerts and key user workflows.
9. Record the go/no-go decision.

## Mandatory smoke workflows
- Login and logout.
- Organization-scoped user access.
- Draft schedule creation and publication.
- Assignment and conflict validation.
- Notification creation and read state.
- Attendance save and finalization.
- Document upload and authorized download.
- Dashboard and report generation.
- Audit event review.

## Rollback triggers
Rollback is required for data corruption, cross-organization access, failed migrations, unavailable authentication, persistent critical errors, or failed core workflows without a safe workaround.
