FROM arm64v8/openjdk:17-ea-16-jdk
ARG JAR_FILE_PATH=build/libs/gollaba-api-v2-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE_PATH} app.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "app.jar"]