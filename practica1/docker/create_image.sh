#!/bin/bash

set -e  # salir si hay error

# Nombre de la imagen
IMAGE_NAME="practica3:1.0.0"

# Ruta del Dockerfile relativo al proyecto
DOCKERFILE_PATH="docker/Dockerfile"

echo "Construyendo imagen Docker: $IMAGE_NAME"
docker build -f "$DOCKERFILE_PATH" -t "$IMAGE_NAME" .
