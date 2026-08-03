# My Publisher Scheduler (MPS)

Version 1.0 release-candidate source package for the MPS scheduling and coordination platform.

## Stack
- Backend: Java 21, Spring Boot 3, Maven
- Web: React, TypeScript, Vite
- Mobile: Flutter
- Database: PostgreSQL 16
- Infrastructure: Docker Compose and Nginx

## Repository layout
- `backend/` Spring Boot modular monolith
- `web/` React web application
- `mobile/` Flutter mobile application shell
- `infra/` Nginx and deployment configuration
- `ops/` backup, verification, performance, and release scripts
- `docs/` architecture, sprint notes, UAT, and operations runbooks
- `releases/` release notes, checksums, and validation evidence

## Development start

```bash
cp .env.example .env
docker compose up --build
```

Default local endpoints:
- Web: `http://localhost:5173`
- Backend API: `http://localhost:8080`
- PostgreSQL: `localhost:5432`

## Production candidate

```bash
cp .env.example .env
# Replace every placeholder secret and disable bootstrap after first controlled setup.
docker compose -f docker-compose.prod.yml config
docker compose -f docker-compose.prod.yml up -d --build
```

The production reverse proxy listens on `${MPS_HTTP_PORT:-8088}`.

## Implemented scope
- Authentication, JWT sessions, password security, rate limiting, and RBAC
- Organization, department, group, user, role, and audit administration
- Schedules, recurrence, calendars, assignments, availability, and conflicts
- Notifications, announcements, attendance, and document versioning
- Dashboards, reports, exports, settings, backups, health, and alerts
- Release, rollback, UAT, performance, and production-readiness tooling

## Release status

This repository is a **release candidate**. It is not evidence that external CI, container builds, staging migrations, security scans, performance tests, restore tests, accessibility reviews, or U[...] 

CI: retrigger commit to run workflow

ci: rerun workflow (user requested)
