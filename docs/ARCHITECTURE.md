# Architecture Notes

MPS uses a modular monolith. Modules remain in one deployable Spring Boot application but must communicate through explicit application services rather than directly accessing another module's persistence internals.

Initial modules:
- auth
- organization
- user
- scheduling
- assignment
- attendance
- communication
- document
- reporting
- audit

All organization-owned records must carry an organization boundary and all authorization must be enforced on the server.


## Scheduling module

Organization-scoped schedule occurrences use a shared series identifier, lifecycle status, optimistic locking, and bounded calendar queries. Recurrence is expanded at creation time for predictable querying and occurrence-specific edits.
