# Etapa de construcción: compila la aplicación usando Maven
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Actualizar apk e instalar Maven
RUN apk update && apk add --no-cache maven

# Copiar los archivos de configuración y código
COPY pom.xml .
COPY src ./src

# Compilar el proyecto omitiendo los tests
RUN mvn clean package -DskipTests

# Etapa final: imagen para ejecutar la aplicación
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copiar el JAR generado desde la etapa build
COPY --from=build /app/target/virtual-karate-dojo-api-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
