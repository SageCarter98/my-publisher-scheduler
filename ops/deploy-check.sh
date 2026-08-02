#!/usr/bin/env bash
set -euo pipefail
base=${1:-http://localhost:8080}; curl -fsS "$base/actuator/health/readiness"; echo; curl -fsS "$base/api/v1/system/info"; echo
