#!/usr/bin/env bash
set -euo pipefail

VERSION=1.0.0
REPO=cuentaDockerHub/nombreApp

# Inicia sesión y publica
docker login
docker push "$REPO:$VERSION"