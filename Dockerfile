# Build stage
FROM maven:3.8.7-openjdk-18 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:17-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# H2 database runs in embedded mode, no external DB needed
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]