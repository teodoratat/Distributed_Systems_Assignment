# Stage 1: Build Stage
FROM maven:3.8.3-openjdk-17 AS builder

# Copy the source code and pom.xml
COPY ./src/ /root/src
COPY ./pom.xml /root/
# COPY checkstyle.xml /root/


# Set the working directory
WORKDIR /root

# Build the application
RUN mvn package
RUN java -Djarmode=layertools -jar /root/target/spring-boot-login-example-0.0.1-SNAPSHOT.jar list
RUN java -Djarmode=layertools -jar /root/target/spring-boot-login-example-0.0.1-SNAPSHOT.jar extract
RUN ls -l /root

# Stage 2: Run Stage
FROM openjdk:17-jdk-slim-buster

# Set environment variables
ENV TZ=UTC
ENV DB_IP=localhost
ENV DB_PORT=3306
ENV DB_USER=root
ENV DB_PASSWORD=andreea2002!
ENV DB_DBNAME=users-ds

# Copy dependencies from the builder stage
COPY --from=builder /root/dependencies/ ./
COPY --from=builder /root/snapshot-dependencies/ ./

# Wait for the database to start
RUN sleep 10

# Copy the application artifacts from the builder stage
COPY --from=builder /root/spring-boot-loader/ ./
COPY --from=builder /root/application/ ./

# Define the entrypoint command
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher", "-XX:+UseContainerSupport", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-XX:MaxRAMFraction=1", "-Xms512m", "-Xmx512m", "-XX:+UseG1GC", "-XX:+UseSerialGC", "-Xss512k", "-XX:MaxRAM=72m"]
