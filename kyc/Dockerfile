FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY build/libs/kyc-0.0.1-SNAPSHOT-all.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
CMD ["--spring.profiles.active=dev"]