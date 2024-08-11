# build app
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY gradlew .
COPY gradle /app/gradle
COPY build.gradle .
COPY settings.gradle .

COPY src /app/src

RUN ./gradlew clean build

# build image
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=build /app/build/libs/*.jar /app/app.jar

CMD ["java", "-jar", "/app/app.jar"]
