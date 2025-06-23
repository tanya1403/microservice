FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY build/libs/public-0.0.1-SNAPSHOT-all.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
