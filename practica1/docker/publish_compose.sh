#!/usr/bin/env bash
set -euo pipefail

# Variables
ARTIFACT=cuentaDockerHub/practica1-compose:1.0.0
COMPOSE_FILE=docker-compose.prod.yml

# Login en Docker y ORAS (si usas ORAS para OCI artifacts)
docker login

# Publicar el Compose como OCI Artifact (requiere 'oras')
oras login docker.io --username "$DOCKERHUB_USER" --password-stdin <<EOF
$DOCKERHUB_PASS
EOF
oras push "docker.io/$ARTIFACT" "$COMPOSE_FILE":application/vnd.docker.compose.config.v1+json