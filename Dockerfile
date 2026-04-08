# Step 1: Build the application using Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean package -DskipTests

# Step 2: Run the application using OpenJDK
FROM eclipse-temurin:21-jre
COPY --from=build /target/*.jar app.jar

# Expose the port Javalin will run on
EXPOSE 5000

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]