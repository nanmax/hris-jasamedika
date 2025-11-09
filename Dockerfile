# Use Eclipse Temurin JDK 17 for better compatibility
FROM eclipse-temurin:17-jdk-alpine

# Install required packages
RUN apk add --no-cache bash curl

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml first for better caching
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn
COPY pom.xml .

# Make Maven wrapper executable and fix line endings
RUN chmod +x mvnw && sed -i 's/\r$//' mvnw

# Download dependencies first (better Docker layer caching)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN ./mvnw clean package -DskipTests -Pproduction -B

# Use a smaller runtime image
FROM eclipse-temurin:17-jre-alpine

# Install bash for startup script
RUN apk add --no-cache bash

WORKDIR /app

# Copy the built jar from build stage
COPY --from=0 /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Set environment variables
ENV PORT=8080
ENV JAVA_OPTS="-Xmx512m -Xss512k -XX:CICompilerCount=2"
ENV VAADIN_PRODUCTION_MODE=true

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:$PORT/ || exit 1

# Run the application
CMD java -Dserver.port=$PORT $JAVA_OPTS -jar app.jar