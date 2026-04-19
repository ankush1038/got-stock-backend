# Use lightweight Java 17 image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy all project files
COPY . .

# Give permission to mvnw
RUN chmod +x mvnw

# Build the project
RUN ./mvnw clean package -DskipTests

# Run the application
CMD ["java", "-jar", "target/got-stock-backend-0.0.1-SNAPSHOT.jar"]