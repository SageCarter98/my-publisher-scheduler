# Sprint 10 — Release Candidate Hardening

## Delivered
- Login endpoint rate limiting with standard retry headers.
- Correlation IDs propagated to responses and structured logs.
- Security response headers and production-safe error behavior.
- Prometheus metrics, liveness, and readiness probes.
- Production Docker Compose topology with read-only backend filesystem and no-new-privileges controls.
- Nginx reverse proxy and frontend security headers.
- JaCoCo test reporting and expanded security-filter unit tests.
- k6 performance smoke profile with p95 and error-rate thresholds.
- Keyboard skip navigation, visible focus states, live status region, and reduced-motion support.
- Release smoke-check script and release-candidate checklist.
- Correction of Sprint 9 migration foreign-key table names before release.

## Release gate
This sprint produces a release candidate, not automatic production approval. CI, UAT, vulnerability review, backup restoration, and environment-specific configuration remain mandatory.
