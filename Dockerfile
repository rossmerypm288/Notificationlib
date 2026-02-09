
# Dockerfile - Notification Library
# Compila la librería y ejecuta los ejemplos

# Imagen base JDK 21
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copia archivos del proyecto
COPY pom.xml .
COPY src ./src

# Instala Maven
RUN apk add --no-cache maven

# Compila la librería y ejecutar tests
RUN mvn clean package

# Ejecuta la clase de ejemplos
CMD ["java", "-cp", "target/classes:target/dependency/*", "com.notify.examples.NotificationExamples"]
