# MPS Version 1.0 Release Candidate Checklist

- [ ] Backend `mvn verify` passes and JaCoCo report is retained.
- [ ] Web `npm ci && npm run build` passes.
- [ ] Container images build without critical vulnerabilities.
- [ ] Flyway migrations apply to a clean and upgraded PostgreSQL database.
- [ ] Authentication, RBAC, and organization-isolation negative tests pass.
- [ ] k6 smoke test meets error-rate and p95 thresholds.
- [ ] Keyboard navigation and primary accessibility checks pass.
- [ ] Database and document backups restore successfully.
- [ ] Liveness, readiness, metrics, logs, and alerts are operational.
- [ ] UAT passes and no unresolved critical defects remain.
- [ ] Production secrets, TLS, retention values, RTO, and RPO are approved.
- [ ] Rollback and incident contacts are confirmed.
