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
![image](/practica1/diagramas/Diagramadenavegacion.png)
### Intrucciones de ejecución
1. Descargar el repositorio y descomprimir el archivo
2. Tener descargado MySQL Workbench: [Descargar](https://dev.mysql.com/downloads/workbench/)
3. Usuario: root\ root
   Contraseña: Trabaj0MySQL.
4. Crear un esquema con nombre: B&D
5. Ejecuta el código en Visual Studio usando Spring Boot Dashboard(añadir como extensión): [Descargar VS](https://code.visualstudio.com/download)
6. Abrelo en el navegador: puedes abrirlo directamente desde VS o escribiendo en el navegador localhost:8080
### Diagrama con las entidades de la base de datos
![image](/practica1/diagramas/diagramabbdd2.png)

### Diagrama de clases y templates
![image](/practica1/diagramas/Diagramadetenplates1.png)

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
  - Tareas: Realizacion general de models y sus controllers y ayuda con los diagramas. Implementar la interfaz error y hacer validación de campos. Crear y conectar la base de datos MySQL. Solventar la actualización del stock y corregir pequeños errores e incongruencias a lo largo del programa. 
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
## Práctica 2: Incorporación de Seguridad y API REST a la aplicación web
### Navegación
#### Navegación de usuarios no registrados
![alt text](/practica1/diagramas/navegacionnoreg.png)
#### Navegación de usuarios registrados
![alt text](/practica1/diagramas/navegacionreg.png)
#### Navegación de administrador
![alt text](/practica1/diagramas/navegacionadmin.png)
### Intrucciones de ejecución
1. Descargar el repositorio y descomprimir el archivo
2. Tener descargado MySQL Workbench: [Descargar](https://dev.mysql.com/downloads/workbench/)
3. Usuario: root\ root
   Contraseña: Trabaj0MySQL.
4. Crear un esquema con nombre: B&D
5. Ejecuta el código en Visual Studio usando Spring Boot Dashboard(añadir como extensión): [Descargar VS](https://code.visualstudio.com/download)
6. Abrelo en el navegador: puedes abrirlo directamente desde VS o escribiendo en el navegador https://localhost:8843
7. Las credenciales de un usuario se pueden crear pero si se quiere entrar como uno ya creado o como un administrador son las siguientes:
- Usuario: 
  - Nombre de usuario: user 
  - Contraseña: pass
- Administrador: 
  - Nombre de usuario: admin 
  - Contraseña: adminpass
### Diagrama con las entidades de la base de datos
![alt text](/practica1/diagramas/diagramabbdd2.png)  
La base de datos no borra los productos eliminados ni los usuarios elimandos, esto se debe para poder seguir almacenando estas entidades para futuras consultas, por ejemplo para guardar el registro de un pedido de un producto que ha sido comprado antes de ser eliminado o un usuario que elimina su cuenta al haber hecho un pedido sin pagar
### Diagrama de clases y templates
![alt text](/practica1/diagramas/Diagramaderest.png)
### Participación de miembros
Paula Marcela Barroso Robleda
  - Tareas: arreglo de problemas anteriores relacionados con correcciones de la primera parte del proyecto, organización y desarrollo de la gestión de los usuarios en la aplicación, implementación de clases RestControllers y posterior realización de funciones en la propia aplicación de Postman. Añadido funcionalidades nuevas como ver los usuarios, borrarlos, editarlos, botón búsqueda de productos, etc.
  - 5 commits mas significativos:
      - [Commit 1](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/commit/9cf2b7ef593b52203fd00c29fed7ce9e70b0eda9)
      - [Commit 2](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/commit/54a995bde86ee4cd543c34d256b3181dbe000392)
      - [Commit 3](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/commit/777faf0fb9e7426a839aa49fb77bf39a766b6c69)
      - [Commit 4](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/commit/19e9bfc3ff2acaf7a44fde23332301085172c649)
      - [Commit 5](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/commit/d36b941bcdfe13b573e532286215d237f031780f)
  - 5 ficheros en los que mas se ha participado:
      - [Fichero 1](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/blob/f450b2a1e1c598878c6e5003ea91f1c8db15423b/practica1/src/main/java/es/codeurjc/practica1/controller/ProductController.java)
      - [Fichero 2](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/blob/f450b2a1e1c598878c6e5003ea91f1c8db15423b/practica1/src/main/java/es/codeurjc/practica1/controller/UserController.java)
      - [Fichero 3](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/tree/f450b2a1e1c598878c6e5003ea91f1c8db15423b/practica1/src/main/resources/templates)
      - [Fichero 4](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/tree/f450b2a1e1c598878c6e5003ea91f1c8db15423b/practica1/src/main/java/es/codeurjc/practica1/controller/rest)
      - [Fichero 5](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/blob/f450b2a1e1c598878c6e5003ea91f1c8db15423b/practica1/src/main/java/es/codeurjc/practica1/model/Product.java)

David Coronado Testa
  - Tareas: He participado en la seguridad con todas las clases en general añadiendo los logins, logouts y roles. En API REST he implementado los DTOS y los controllers. Completado paginación.
  - 5 commits mas significativos:
      - [Commit 1](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/commit/e1279bd1a09bdafe13630335228fd8f825101a5f) 
      - [Commit 2](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/commit/f103e1e245cbeda6c76e06f6ed0958f527d396e5)
      - [Commit 3](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/commit/152e330fd9bf3eb244ac6e76aff52c1373e75119)
      - [Commit 4](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/commit/75075dc207e6c70d35c911008e7507b729fe2adf)
      - [Commit 5](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/commit/e35340d40d08c542cd4862f1372e274c08ce859b)
  - 5 ficheros en los que mas se ha participado:
      - [Fichero 1](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/blob/main/practica1/src/main/java/es/codeurjc/practica1/security/SecurityConfig.java)
      - [Fichero 2](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/blob/main/practica1/src/main/java/es/codeurjc/practica1/controller/rest/ProductRestController.java)
      - [Fichero 3](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/blob/main/practica1/src/main/java/es/codeurjc/practica1/controller/ProductController.java)
      - [Fichero 4](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/blob/main/practica1/src/main/java/es/codeurjc/practica1/controller/rest/UserRestController.java)
      - [Fichero 5](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/blob/main/practica1/src/main/java/es/codeurjc/practica1/service/UserService.java)

Juan José Villanueva Molina
  - Tareas: Colaborar en la parte de seguridad, ayudar a los problemas que surgieron al añadir la seguridad, gestión de los usuarios, preparar la api y la autorización para el postman, documentación, diagramas y Readme.
  - 5 commits mas significativos:
      - [Commit 1](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/commit/32729f708fbf346e879ba5ce2c54c127c9bc2af5)
      - [Commit 2](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/commit/9fdcb6179f1f06ddc847bd8a41e4cdb445851ab1)
      - [Commit 3](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/commit/0977eeb45e3c14ad2571c001f723c28ec967aa57)
      - [Commit 4](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/commit/90bb9854fc6e257fb2baeb074d07064c5f195d38)
      - [Commit 5](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/commit/eb5a71292d911e5a06e721a6833058117786b02d)
  - 5 ficheros en los que mas se ha participado:
      - [Fichero 1](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/blob/main/README.md)
      - [Fichero 2](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/blob/main/practica1/src/main/java/es/codeurjc/practica1/security/SecurityConfig.java)
      - [Fichero 3](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/blob/main/practica1/src/main/java/es/codeurjc/practica1/controller/UserController.java)
      - [Fichero 4](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/blob/main/practica1/src/main/java/es/codeurjc/practica1/controller/ProductController.java)
      - [Fichero 5](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/blob/main/practica1/src/main/java/es/codeurjc/practica1/controller/rest/ProductRestController.java)
***

Pasos para crear la imagen (jar, etc):


EN LOCAL:
docker login

.\docker\create_image.bat

![Captura de pantalla 2025-05-20 100811]((https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/blob/main/Captura%20de%20pantalla%202025-05-20%20100811.png))

![Captura de pantalla 2025-05-20 100811]((https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/blob/main/Captura%20de%20pantalla%202025-05-20%20100840.png))

.\docker\publish_image.bat

![Captura de pantalla 2025-05-20 100811]((https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/blob/main/Captura%20de%20pantalla%202025-05-20%20100653.png))

![Captura de pantalla 2025-05-20 100811]((https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/blob/main/Captura%20de%20pantalla%202025-05-20%20100729.png))


docker compose -f docker/docker-compose.yml up
.\docker\create_image.bat // desde practica1 
mvn spring-boot:build-image -DskipTests -Dspring-boot.build-image.imageName=dvcoronado/practica3:1.0.0
docker run -p 8443:8443 dvcoronado/practica3:1.0.0

Ver BBDD:
sudo docker exec -it mysql-db mysql -u root -p

MAQUINA 1:
sudo docker login

sudo docker run -d --name practica3-app -e DB_HOST=IP_O_HOSTNAME_DEL_SERVIDOR_MYSQL -e DB_PORT=3306 -e DB_NAME=myapp_db -e DB_USER=root -e DB_PASSWORD=MySQL0Password. -p 8443:8443 dvcoronado/practica3:1.0.0

![Captura de pantalla 2025-05-20 100811]((https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/blob/main/Captura%20de%20pantalla%202025-05-20%20100133.png))

MAQUINA 2:
sudo docker login

sudo docker run -d --name mysql-db   -e MYSQL_ROOT_PASSWORD=MySQL0Password.   -e MYSQL_DATABASE=myapp_db   -v db_data:/var/lib/mysql   -p 3306:3306   mysql:9.2
 sudo docker exec -it mysql-db mysql -u root -p

 ![Captura de pantalla 2025-05-20 100811](https://github.com/SSDD-2025/practica-sistemas-distribuidos-2025-grupo-5/blob/main/Captura%20de%20pantalla%202025-05-20%20100208.png))
 
 SHOW DATABASES;
