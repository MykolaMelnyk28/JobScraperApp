FROM maven:3.9.6-amazoncorretto-21 AS builder

COPY . /app/.

WORKDIR /app

RUN mvn clean package

FROM amazoncorretto:21

COPY --from=builder /app/target/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]