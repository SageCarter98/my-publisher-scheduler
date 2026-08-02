#!/usr/bin/env bash
set -euo pipefail
backup=${1:?Usage: restore-verify.sh BACKUP_DIRECTORY}; cd "$backup"; sha256sum -c SHA256SUMS; test -s database.dump; test -s documents.tar.gz; echo "Backup integrity verified: $backup"
