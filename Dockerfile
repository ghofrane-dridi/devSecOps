# Étape 1 : builder le jar avec Maven
FROM maven:3.9.3-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Étape 2 : image runtime pour exécuter le jar
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/devsecops-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
