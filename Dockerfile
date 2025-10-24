# Imagen base con Java 17 (puedes ajustar si usas otra versión)
FROM eclipse-temurin:17-jdk-alpine

# Carpeta de trabajo
WORKDIR /app

# Copia el archivo .jar desde la carpeta target al contenedor
COPY target/*.jar app.jar

# Expone el puerto que usa Spring Boot
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
