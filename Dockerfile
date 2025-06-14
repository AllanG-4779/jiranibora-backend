# ---------- Stage 1: Build the JAR ----------
FROM eclipse-temurin:17-jdk AS build

# Install Maven
RUN apt-get update && apt-get install -y maven

# Set work directory inside container
WORKDIR /app

# Copy Maven project files
COPY pom.xml .
COPY src ./src

# Package the application (skip tests for faster build)
RUN mvn clean package -DskipTests

# ---------- Stage 2: Run the application ----------
FROM eclipse-temurin:17-jdk

# Set working directory
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose application port (adjust as needed)
EXPOSE 8080

# Start the app
ENTRYPOINT ["java", "-jar", "app.jar"]
