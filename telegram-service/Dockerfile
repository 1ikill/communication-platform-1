# Use pre-built TDLib base image (build once with build-tdlib-base.ps1)
ARG ACR_SERVER
FROM ${ACR_SERVER}/telegram-tdlib-base:latest AS tdlib-builder

########################################
# Build the application
########################################
FROM tdlib-builder AS app-builder

WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

########################################
# Final runtime image
########################################
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy TDLib native libraries from base image
COPY --from=tdlib-builder /tdlib/*.so /app/lib/
COPY --from=tdlib-builder /tdlib/tdlib-1.8.42.jar /app/lib/

# Copy application JAR from app-builder
COPY --from=app-builder /app/target/*.jar app.jar

ENV LD_LIBRARY_PATH="/app/lib"

EXPOSE 8082
ENTRYPOINT ["java","-Djava.library.path=/app/lib","-jar","app.jar"]
