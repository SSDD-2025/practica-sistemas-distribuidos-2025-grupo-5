#!/bin/bash

set -e  # Sale en caso de error

# Configura estas variables antes de ejecutar
IMAGE_NAME="practica3"
IMAGE_TAG="1.0.0"
DOCKER_USERNAME="dvcoronado"  # <- cámbialo por tu usuario real

# Nombre completo de la imagen
FULL_IMAGE_NAME="$DOCKER_USERNAME/$IMAGE_NAME:$IMAGE_TAG"

echo "Autenticando en Docker Hub..."
docker login

echo "Etiquetando imagen como $FULL_IMAGE_NAME"
docker tag "$IMAGE_NAME:$IMAGE_TAG" "$FULL_IMAGE_NAME"

echo "Publicando imagen en Docker Hub..."
docker push "$FULL_IMAGE_NAME"

echo "Imagen publicada con éxito: $FULL_IMAGE_NAME"
