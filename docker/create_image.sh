#!/bin/bash

set -e  # salir si hay error

# Nombre de la imagen
IMAGE_NAME="practica3:1.0.0"

# Ruta del Dockerfile relativo al proyecto
DOCKERFILE_PATH="docker/Dockerfile"

echo "Construyendo imagen Docker: $IMAGE_NAME"
docker build -f "$DOCKERFILE_PATH" -t "$IMAGE_NAME" .

#!/usr/bin/env bash
# set -euo pipefail

# # Variables
# VERSION=1.0.0
# REPO=cuentaDockerHub/nombreApp
# SCRIPT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)

# # Construye imagen usando Dockerfile y multi-stage build
# docker build \
#   --file "$SCRIPT_DIR/Dockerfile" \
#   --tag "$REPO:$VERSION" \
#   "$SCRIPT_DIR/.."