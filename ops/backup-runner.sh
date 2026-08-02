#!/usr/bin/env bash
set -euo pipefail
: "${SPRING_DATASOURCE_URL:?}" "${SPRING_DATASOURCE_USERNAME:?}" "${PGPASSWORD:?}" "${MPS_BACKUPS_PATH:=./data/backups}" "${MPS_DOCUMENTS_PATH:=./data/documents}"
ts=$(date -u +%Y%m%dT%H%M%SZ); out="$MPS_BACKUPS_PATH/$ts"; mkdir -p "$out"
url=${SPRING_DATASOURCE_URL#jdbc:}; pg_dump "$url" -U "$SPRING_DATASOURCE_USERNAME" -Fc -f "$out/database.dump"
tar -czf "$out/documents.tar.gz" -C "$MPS_DOCUMENTS_PATH" .
sha256sum "$out/database.dump" "$out/documents.tar.gz" > "$out/SHA256SUMS"
echo "Backup completed: $out"
