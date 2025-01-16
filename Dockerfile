FROM eclipse-temurin:21 AS build
COPY . .
RUN ./gradlew clean build -x test

FROM openjdk:21-jdk
COPY --from=build /build/libs/FirebaseLearnAuth-0.0.1-SNAPSHOT.jar RemoteControlApp.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","RemoteControlApp.jar"]
