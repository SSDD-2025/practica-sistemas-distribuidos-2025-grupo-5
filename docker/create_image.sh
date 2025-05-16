#!/usr/bin/env bash
set -euo pipefail

# Variables
VERSION=1.0.0
REPO=cuentaDockerHub/nombreApp
SCRIPT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)

# Construye imagen usando Dockerfile y multi-stage build
docker build \
  --file "$SCRIPT_DIR/Dockerfile" \
  --tag "$REPO:$VERSION" \
  "$SCRIPT_DIR/.."