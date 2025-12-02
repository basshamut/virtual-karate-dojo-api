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

# Instalar curl y file para descargar el agente de OpenTelemetry
RUN apk add --no-cache curl file

# Descargar el agente de OpenTelemetry con validación
ARG OTEL_AGENT_VERSION=2.9.0
RUN curl -L -f -o opentelemetry-javaagent.jar \
    "https://repo1.maven.org/maven2/io/opentelemetry/javaagent/opentelemetry-javaagent/${OTEL_AGENT_VERSION}/opentelemetry-javaagent-${OTEL_AGENT_VERSION}.jar" \
    && ls -la opentelemetry-javaagent.jar \
    && file opentelemetry-javaagent.jar \
    && test -s opentelemetry-javaagent.jar \
    && [ $(stat -c%s opentelemetry-javaagent.jar) -gt 1000000 ] \
    && echo "OpenTelemetry agent downloaded successfully"

# Copiar el JAR generado desde la etapa build
COPY --from=build /app/target/virtual-karate-dojo-api-0.0.1-SNAPSHOT.jar app.jar

# Variables de entorno por defecto para OpenTelemetry (pueden ser sobrescritas en K8s)
ENV OTEL_SERVICE_NAME=virtual-karate-dojo-api
ENV OTEL_SERVICE_VERSION=0.0.1-SNAPSHOT
ENV OTEL_RESOURCE_ATTRIBUTES="service.name=virtual-karate-dojo-api,service.version=0.0.1-SNAPSHOT"
ENV OTEL_EXPORTER_OTLP_PROTOCOL=grpc
ENV OTEL_TRACES_EXPORTER=otlp
ENV OTEL_METRICS_EXPORTER=none
ENV OTEL_LOGS_EXPORTER=none
ENV OTEL_INSTRUMENTATION_MICROMETER_ENABLED=true
ENV OTEL_INSTRUMENTATION_SPRING_WEB_ENABLED=true
ENV OTEL_INSTRUMENTATION_SPRING_WEBMVC_ENABLED=true
ENV OTEL_INSTRUMENTATION_MONGO_ENABLED=true
ENV OTEL_INSTRUMENTATION_HTTP_CLIENT_ENABLED=true
ENV OTEL_INSTRUMENTATION_JDBC_ENABLED=true

EXPOSE 8080

# Verificar que el agente existe y es válido
RUN test -f opentelemetry-javaagent.jar && test -s opentelemetry-javaagent.jar || { echo "OpenTelemetry agent not found or empty!"; exit 1; }

# Crear un script de inicio que verifique el agente antes de usar
RUN echo '#!/bin/sh' > /app/start.sh && \
    echo 'if [ ! -f opentelemetry-javaagent.jar ] || [ ! -s opentelemetry-javaagent.jar ]; then' >> /app/start.sh && \
    echo '  echo "Error: OpenTelemetry agent not found or empty!"' >> /app/start.sh && \
    echo '  exit 1' >> /app/start.sh && \
    echo 'fi' >> /app/start.sh && \
    echo 'exec java -javaagent:opentelemetry-javaagent.jar -jar app.jar' >> /app/start.sh && \
    chmod +x /app/start.sh

ENTRYPOINT ["/app/start.sh"]
