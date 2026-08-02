#!/usr/bin/env bash
set -euo pipefail
: "${BASE_URL:=http://localhost:8088}"
for path in / /actuator/health/liveness /actuator/health/readiness; do
  code=$(curl -fsS -o /tmp/mps-response -w '%{http_code}' "$BASE_URL$path")
  test "$code" = "200" || { echo "FAILED $path ($code)"; exit 1; }
  echo "PASS $path"
done
