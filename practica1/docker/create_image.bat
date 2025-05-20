@echo off
echo ========================================
echo Compilando el proyecto con Maven...
echo ========================================
call mvn clean package -DskipTests

if %ERRORLEVEL% NEQ 0 (
    echo Error al compilar el proyecto Maven. Abortando.
    exit /b %ERRORLEVEL%
)

echo Compilación Maven completada.

echo ========================================
echo Construyendo la imagen Docker...
echo ========================================
docker build -t paulam04/practica3:1.0.0 -f docker/Dockerfile .

if %ERRORLEVEL% NEQ 0 (
    echo Error al construir la imagen Docker.
    exit /b %ERRORLEVEL%
)

echo Imagen Docker construida correctamente.