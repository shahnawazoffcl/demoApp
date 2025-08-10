# =========================
# Stage 1: Build
# =========================
FROM maven:3.9.6-amazoncorretto-17 AS build

WORKDIR /app

# Copy Maven files first for caching
COPY pom.xml .

# Copy the source code
COPY src src

# Build the application
RUN mvn clean package -DskipTests

# =========================
# Stage 2: Runtime
# =========================
FROM amazoncorretto:17

# Install curl (Amazon Linux uses yum)
RUN yum install -y curl && yum clean all

# Create user

WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/SchoolVroom-0.0.1-SNAPSHOT.jar app.jar

# Change ownership

# Switch to non-root user
EXPOSE 8083

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8083/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
