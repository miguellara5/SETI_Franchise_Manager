# Stage 1: Build
FROM eclipse-temurin:25-jdk-alpine AS builder

RUN apk add --no-cache bash git dos2unix

WORKDIR /build

COPY . .

RUN dos2unix gradlew
RUN chmod +x gradlew

# Ejecutamos el build
RUN ./gradlew bootJar -x test -x validateStructure --no-daemon

# Stage 2: Runtime
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# Optimizaciones de JVM
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copiar el JAR generado
COPY --from=builder --chown=appuser:appgroup /build/applications/app-service/build/libs/*.jar app.jar

VOLUME /tmp

HEALTHCHECK --interval=30s --timeout=10s --start-period=15s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

USER appuser
EXPOSE 8080

# Ajuste en ENTRYPOINT para activar el perfil de producción (archivo YAML 3)
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --spring.profiles.active=prod --server.port=${PORT:-8080}"]