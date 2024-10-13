FROM openjdk:17
WORKDIR /app
VOLUME /tmp
COPY Tasty-0.0.1-SNAPSHOT.jar .
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "Tasty-0.0.1-SNAPSHOT.jar"]