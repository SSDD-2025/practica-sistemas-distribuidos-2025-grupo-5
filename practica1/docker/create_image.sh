#!/bin/bash

echo "========================================"
echo "Compilando el proyecto con Maven..."
echo "========================================"

# Ejecutar Maven sin tests
mvn clean package -DskipTests
if [ $? -ne 0 ]; then
  echo "Error al compilar el proyecto Maven. Abortando."
  exit 1
fi

echo "Compilación Maven completada."

echo "========================================"
echo "Construyendo la imagen Docker..."
echo "========================================"

docker build -t paulam04/practica3:1.0.0 -f docker/Dockerfile .
if [ $? -ne 0 ]; then
  echo "Error al construir la imagen Docker."
  exit 1
fi

echo "Imagen Docker construida correctamente."
