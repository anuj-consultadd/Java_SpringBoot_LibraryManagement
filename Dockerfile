FROM openjdk:21
WORKDIR /app

COPY target/management-0.0.1-SNAPSHOT.jar /app/management-0.0.1-SNAPSHOT.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "management-0.0.1-SNAPSHOT.jar"]