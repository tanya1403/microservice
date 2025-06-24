FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy the bootJar output, usually named without `-all`
COPY build/libs/kyc-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

CMD ["--spring.profiles.active=dev"]
