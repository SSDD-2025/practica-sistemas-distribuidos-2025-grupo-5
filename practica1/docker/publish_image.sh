#!/bin/bash

set -e  # Salir si ocurre algún error

# Configura estas variables si cambias el nombre o tag de la imagen
IMAGE_NAME="practica3"
IMAGE_TAG="1.0.0"
DOCKER_USERNAME="paulam04"

# Nombre completo para Docker Hub
FULL_IMAGE_NAME="$DOCKER_USERNAME/$IMAGE_NAME:$IMAGE_TAG"

echo "========================================"
echo "Autenticando en Docker Hub..."
echo "========================================"
docker login

echo "========================================"
echo "Etiquetando imagen como $FULL_IMAGE_NAME"
echo "========================================"

echo "========================================"
echo "Publicando imagen en Docker Hub..."
echo "========================================"
docker push "$FULL_IMAGE_NAME"

echo "========================================"
echo "Imagen publicada con éxito: $FULL_IMAGE_NAME"
