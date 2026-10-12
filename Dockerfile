FROM eclipse-temurin:24-jre

WORKDIR /app

COPY target/*.jar mindcare-diary-0.0.1-SNAPSHOT.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "mindcare-diary-0.0.1-SNAPSHOT.jar"]