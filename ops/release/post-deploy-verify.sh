#!/usr/bin/env bash
set -euo pipefail
BASE_URL="${BASE_URL:-http://localhost}"
retry() {
  local url="$1"
  for _ in {1..30}; do
    if curl -fsS "$url" >/dev/null; then return 0; fi
    sleep 2
  done
  echo "Verification failed: $url" >&2
  return 1
}
retry "$BASE_URL/actuator/health/liveness"
retry "$BASE_URL/actuator/health/readiness"
echo "Health probes passed. Execute authenticated smoke and UAT workflows before approval."
