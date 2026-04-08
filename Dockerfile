# Step 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean package -DskipTests

# Step 2: Run
FROM eclipse-temurin:21-jre
# Copy the final shaded jar
COPY --from=build /target/Back-End.jar app.jar

EXPOSE 5000
# Add a flag to handle potential networking issues in containers
ENTRYPOINT ["java", "-Djava.net.preferIPv4Stack=true", "-jar", "app.jar"]