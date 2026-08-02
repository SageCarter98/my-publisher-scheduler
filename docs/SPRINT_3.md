# Sprint 3 — Organization, People, Roles, and Audit

## Delivered

- Organization profile read/update APIs with timezone validation
- Department create, list, update, and archive workflows
- Group create, list, update, and archive workflows
- Organization-scoped user listing, creation, update, archive, and restore
- Department and group associations on user profiles
- Controlled role assignment with Super Administrator delegation protection
- Append-only audit capture for organization, structure, user, and role changes
- Organization-scoped audit review endpoint
- PostgreSQL migration `V3__organization_people_and_audit.sql`

## API surface

- `GET|PUT /api/v1/organization`
- `GET|POST /api/v1/departments`
- `PUT|DELETE /api/v1/departments/{id}`
- `GET|POST /api/v1/groups`
- `PUT|DELETE /api/v1/groups/{id}`
- `GET|POST /api/v1/users`
- `PUT|DELETE /api/v1/users/{id}`
- `POST /api/v1/users/{id}/restore`
- `PUT /api/v1/users/{id}/roles`
- `GET /api/v1/audit`

All operational queries are restricted by the authenticated organization identifier. Authorization remains server-side through Spring method security.
