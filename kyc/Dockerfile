FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY build/libs/kyc-0.0.1-SNAPSHOT-all.jar app.jar

EXPOSE 8080

# 👇 Set the active profile to 'dev'
ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "app.jar"]
