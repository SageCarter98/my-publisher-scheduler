# MPS 1.0.0-rc2 Local Validation Report

## Result
**Status: Conditionally ready for external CI and staging validation.**

## Checks completed in the packaging environment
- Release archive structure inspected.
- Shell scripts passed `bash -n` syntax validation.
- Java source brace and file-structure checks completed.
- Flyway migrations V1 through V9 are present and sequential.
- Production and development YAML files parsed successfully.
- Release checksum manifest was regenerated after corrections.
- ZIP integrity verification completed.

## Corrections included in rc2
1. Production compose now uses the same `MPS_JWT_SECRET` variable expected by Spring configuration and documents it consistently.
2. Production compose now passes JWT lifetime, issuer, bootstrap, document, and backup settings explicitly.
3. A persistent production backup volume was added.
4. GitHub Actions no longer calls `npm ci` without a lockfile; it uses a clean package installation step until a lockfile is generated and committed in a networked environment.
5. README sprint inconsistencies were replaced with release-candidate instructions.
6. Release checksums were refreshed.

## Environment limitations
The packaging environment does not provide Maven or Docker. Its internal npm mirror also lacks `@types/react`. Therefore these gates remain **not executed here**:
- Maven compile and test suite.
- React dependency installation and production build.
- Container image builds.
- Trivy vulnerability scan.
- PostgreSQL migration execution.
- End-to-end API and UI tests.
- k6 performance testing.
- Backup restoration.
- UAT and accessibility review.

## Required next decision
Run the included GitHub Actions workflow and deploy rc2 to a staging environment. Production approval remains blocked until all go-live checklist items are supported by evidence.
