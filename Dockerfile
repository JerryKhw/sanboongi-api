FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY build/libs/app.jar app.jar
COPY src/main/resources/base.hwp src/main/resources/base.hwp

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
