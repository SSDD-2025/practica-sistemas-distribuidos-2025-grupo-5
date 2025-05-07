# ---------------------
# Etapa de construcción
# ---------------------
    FROM maven:3.9.4-eclipse-temurin-17 AS builder

    # Crear directorio de trabajo dentro del contenedor
    WORKDIR /app
    
    # Copiar el código fuente del proyecto (asume estructura Maven estándar)
    COPY .. .
    
    # Compilar el proyecto sin tests y generar el .jar
    RUN mvn clean package -DskipTests
    
    # ------------------
    # Etapa de ejecución
    # ------------------
    FROM eclipse-temurin:17-jdk-alpine
    
    # Directorio de trabajo para la aplicación
    WORKDIR /app
    
    # Puerto en el que correrá la aplicación
    EXPOSE 8443
    
    # Añadir el .jar desde el build anterior (multi-stage)
    ARG JAR_FILE=target/*.jar
    COPY --from=builder /app/${JAR_FILE} app.jar
    
    # Comando para ejecutar la aplicación
    ENTRYPOINT ["java", "-jar", "/app/app.jar"]