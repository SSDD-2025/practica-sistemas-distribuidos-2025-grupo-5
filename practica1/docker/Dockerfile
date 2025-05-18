#################################################
# Imagen base para el contenedor de compilación
#################################################
FROM maven:3.9.9-eclipse-temurin-21-jammy AS builder

# Define el directorio de trabajo donde ejecutar comandos
WORKDIR /practica1

# Copia las dependencias del proyecto
COPY pom.xml /practica1/

# Descarga las dependencias del proyecto
RUN mvn dependency:go-offline

# Copia el código del proyecto
COPY /src /practica1/src

# Compila proyecto
RUN mvn clean package -DskipTests

#################################################
# Imagen base para el contenedor de la aplicación
#################################################
FROM eclipse-temurin:21-jre

# Define el directorio de trabajo donde se encuentra el JAR
WORKDIR /usr/src/app/

# Copia el JAR del contenedor de compilación
COPY --from=builder /practica1/target/practica3-1.0.0.jar /usr/src/app/app.jar

# Comando por defecto
ENTRYPOINT [ "java", "-jar", "app.jar" ]

# Parametro por defecto
CMD ["--server.port=8443"]
