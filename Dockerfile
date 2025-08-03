FROM maven:3.9.3-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Étape 2 : image runtime pour exécuter le jar
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copier le JAR généré (option 1: version exacte)
# COPY --from=build /app/target/devsecops-1.0.1-SNAPSHOT.jar app.jar

# OU option 2: copier n'importe quel jar dans target (plus flexible)
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
