#!/usr/bin/env bash
set -euo pipefail
required=(docker-compose.prod.yml .env.example backend/pom.xml web/package.json)
for f in "${required[@]}"; do [[ -f "$f" ]] || { echo "Missing $f" >&2; exit 1; }; done
command -v docker >/dev/null || { echo "docker is required" >&2; exit 1; }
docker compose version >/dev/null
[[ -n "${POSTGRES_PASSWORD:-}" ]] || echo "WARNING: POSTGRES_PASSWORD is not exported in this shell"
[[ -n "${JWT_SECRET:-}" ]] || echo "WARNING: JWT_SECRET is not exported in this shell"
echo "Preflight structure checks passed. External CI, secrets, capacity and backup approvals remain required."
