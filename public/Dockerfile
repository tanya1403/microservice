FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY build/libs/public-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

CMD ["java", "-jar", "app.jar"]
