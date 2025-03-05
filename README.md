[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/D1C1HU9V)
# Grupo 5
***
## Preparación: Definición de las funcionalidades de la web
### Nombre de la aplicacion B&S
### Integrantes del equipo 
Paula Marcela Barroso Robleda
  - Correo de la universidad: pm.barroso.2022@alumnos.urjc.es
  - Cuenta de GitHub: [paulaM04](https://github.com/paulaM04)

David Coronado Testa
  - Correo de la universidad: d.coronado.2022@alumnos.urjc.es
  - Cuenta de GitHub: [dvcoronado](https://github.com/dvcoronado)

Juan José Villanueva Molina
  - Correo de la universidad: jj.villanueva.2022@alumnos.urjc.es
  - Cuenta de GitHub: [juanjouni](https://github.com/juanjouni)
### Entidades
La aplicación consta de varias entidades. Existe una entidad user que se relaciona con la entidad review de manera que un usuario puede escribir una reseña de un producto y un usuario tiene una lista de las reseñas hechas. La entidad producto se relaciona con review ya que un producto puede tener varias reseñas, tambien se relaciona con la entidad user ya que un usuario puede publicar varios productos y por ultimo se relaciona con la entidad order ya que se pueden ordenar varios productos. La entidad order esta relacionada con la entidad user ya que se pueden realizar varias peticiones.  

![image](https://github.com/user-attachments/assets/8b1ebb54-e35c-484c-9dac-b564f37798b0)

### Permisos de los usuarios
Los permisos de los usuarios se dividen dependiendo de su registro. Si un usuario no esta registrado solo podrá ver productos y si desea comprar se tendrá que registrar. El administrador tiene controlo total sobre la aplicación, pudiendo añadir o eliminar tanto usuarios como productos.
### Imágenes
Los productos tendran asociada una imágen.
***
## Práctica 1: Web con HTML y Base de Datos
### Navegación

### Intrucciones de ejecución
1. Descargar el repositorio y descomprimir el archivo
2. Tener descargado MySQL Workbench: [Descargar](https://dev.mysql.com/downloads/workbench/)
3. Usuario: root\ 
   Contraseña: 
4. Crear un esquema con nombre: B&D
5. Ejecuta el código en Visual Studio usando Spring Boot Dashboard(añadir como extensión): [Descargar VS](https://code.visualstudio.com/download)
6. Abrelo en el navegador: puedes abrirlo directamente desde VS o escribiendo en el navegador localhost:8080
### Diagrama con las entidades de la base de datos
![image](https://github.com/user-attachments/assets/467937e5-20e9-4130-a058-2aa6ad08e2cc)

### Diagrama de clases y templates
![image](https://github.com/user-attachments/assets/35c83160-18d2-43ea-a6e0-e377525178ce)

### Participación de miembros
Paula Marcela Barroso Robleda
  - Tareas: implementación general de las clases, entre las que destacan el productController donde se realizaron las redirecciones y funcionalidades solicitadas. Además, gran participación en el mapeo y creación de entidades. Consolidación de los html con el css.
  - 5 commits mas significativos:
      - [Commit 1](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/commit/d6800596b75fcf45c7a99bb35c241a2fcfbf6ba6)
      - [Commit 2](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/commit/ef9d78302c98e20298043d632421c1a4ee7c4208)
      - [Commit 3](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/commit/bdb9765caefcee6a730d93d610cbc38496bb4e06)
      - [Commit 4](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/commit/e95afbc46eda3a9a03a84e9338f3028c92cf2802)
      - [Commit 5](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/commit/5e0fe537a384dcd9ad7f8ad808c09078191bff60)
  - 5 ficheros en los que mas se ha participado:
      - [Fichero 1](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/blob/main/practica1/src/main/java/es/codeurjc/practica1/controller/ProductController.java)
      - [Fichero 2](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/blob/main/practica1/src/main/java/es/codeurjc/practica1/service/DatabaseInitializer.java)
      - [Fichero 3](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/blob/main/practica1/src/main/resources/static/style.css)
      - [Fichero 4](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/blob/main/practica1/src/main/java/es/codeurjc/practica1/service/ProductService.java)
      - [Fichero 5](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/blob/main/practica1/src/main/java/es/codeurjc/practica1/model/Product.java)

David Coronado Testa
  - Tareas:
  - 5 commits mas significativos:
      - [Commit 1](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/commit/4b049726490e5100933b12c4a40ad77a8cf52ce9)
      - [Commit 2](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/commit/077e2e168fa7606b19ec78c3e49ef0c8b1204f9c)
      - [Commit 3](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/commit/7cd3ce5071a1a5a3258d6d74a36cdf9ab67ea964)
      - [Commit 4](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/commit/b40dc773ebee72d0076e10d02c444dde883c651e)
      - [Commit 5](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/commit/349be22100c2d404209cdc93715f7f49fd1eba0c)
  - 5 ficheros en los que mas se ha participado:
      - [Fichero 1](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/blob/main/practica1/src/main/java/es/codeurjc/practica1/controller/ProductController.java)
      - [Fichero 2](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/blob/main/practica1/src/main/java/es/codeurjc/practica1/model/Product.java)
      - [Fichero 3](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/blob/main/practica1/src/main/java/es/codeurjc/practica1/model/User.java)
      - [Fichero 4](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/blob/main/practica1/src/main/resources/templates/reviews.html)
      - [Fichero 5](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/blob/main/practica1/src/main/resources/templates/error.html)

Juan José Villanueva Molina
  - Tareas: Documentar la práctica rellenando el readme, además de crear el diagrama de templates. Hacer que la entidad producta se pueda editar. Colaborar en el desarrollo general de las demas clases. Crear la interfaz newReview y hacer que se pueda crear una review. Rellenar la rúbrica.
  - 5 commits mas significativos:
      - [Commit 1](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/commit/f8e1a71eb79823efa86340fc307f0c830d3c0413)
      - [Commit 2](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/commit/e7d863a6650a0412cf3e0716288287563db0d777)
      - [Commit 3](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/commit/2822d5b4ca8b2940bb84e23c4518d12edbf12d06)
      - [Commit 4](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/commit/9bdaddf7efa007340e63b10ed0b7cbaf1229e468)
      - [Commit 5](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/commit/f285c793efc694f3d54c057355eb811bef703eee)
  - 5 ficheros en los que mas se ha participado:
      - [Fichero 1](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/blob/main/practica1/src/main/java/es/codeurjc/practica1/controller/ProductController.java)
      - [Fichero 2](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/blob/main/practica1/src/main/resources/templates/newReview.html)
      - [Fichero 3](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/blob/main/practica1/src/main/resources/templates/reviews.html)
      - [Fichero 4](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/blob/main/README.md)
      - [Fichero 5](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/blob/main/practica1/src/main/java/es/codeurjc/practica1/model/Review.java)
***
