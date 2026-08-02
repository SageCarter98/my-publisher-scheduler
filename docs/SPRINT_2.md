# Sprint 2 — Authentication and Access Foundation

## Delivered

- PostgreSQL identity and access migration with roles, permissions, user-role mappings, refresh tokens, lockout fields, and indexes.
- Argon2 password hashing.
- JWT access tokens with organization and authority claims.
- Opaque rotating refresh tokens stored only as SHA-256 hashes.
- Login, refresh, logout, logout-all, and current-user endpoints.
- Stateless Spring Security filter chain and method-security support.
- Account lockout controls and expired-lock recovery.
- Bootstrap administrator controlled through environment variables.
- React login and protected dashboard shell.
- Starter token hashing test.

## API endpoints

- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh`
- `POST /api/v1/auth/logout`
- `POST /api/v1/auth/logout-all`
- `GET /api/v1/auth/me`

## Security notes

- Replace all development credentials before any shared or production deployment.
- Refresh tokens are rotated on every successful refresh.
- Access tokens are intentionally short-lived.
- Organization scoping is embedded in the authenticated principal and must be enforced in every organization-owned repository query.
- A future sprint will add password-reset delivery, security audit persistence, and administrator user lifecycle endpoints.
