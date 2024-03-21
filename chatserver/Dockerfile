# Stage 1: Build the application
FROM maven:3.8.4-openjdk-17 AS builder

# Copy the Maven project files
COPY ./pom.xml /build/
COPY ./src /build/src/

# Set working directory
WORKDIR /build

# Build the application
RUN mvn -Dmaven.test.skip=true package

# Stage 2: Create the runtime image
FROM openjdk:17-jdk-slim AS runtime

# Set environment variables
ENV SERVER_PORT=8084

# Copy the compiled application from the builder stage
COPY --from=builder /build/target/*.jar /app/app.jar

# Copy the application.properties file
COPY ./src/main/resources/application.properties /app/


# Set working directory
WORKDIR /app

# Expose the server port
EXPOSE $SERVER_PORT

# Command to run the application
CMD ["java", "-jar", "app.jar", "--spring.config.location=file:/app/application.properties"]
