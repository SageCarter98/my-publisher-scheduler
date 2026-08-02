# Sprint 7 — Attendance and Documents

## Delivered

- Draft attendance registers per schedule
- One attendance entry per member and event
- Present, absent, excused, and late statuses
- Finalization protection and privileged correction workflow
- Audit events for attendance saves, finalization, and correction
- Secure multipart document upload with 10 MB limit and allowlisted media types
- SHA-256 file integrity metadata
- Organization, department, group, role, and user audience scopes
- Authorized download, archive, replacement, and full version history
- Persistent local/Docker document storage abstraction
- Attendance and document web workspaces

## Security notes

- All attendance and document operations enforce organization boundaries.
- Finalized attendance can only be changed through the correction endpoint.
- File names are normalized and storage paths are constrained beneath the configured root.
- File access is evaluated server-side for every list, metadata, and download operation.
- Production deployments should replace local object storage with an S3-compatible adapter and malware scanner.
