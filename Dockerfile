FROM openjdk:21-jdk
WORKDIR /app
COPY build/libs/FirebaseLearnAuth-0.0.1-SNAPSHOT.jar RemoteControlApp.jar
CMD ["java", "-jar", "RemoteControlApp.jar"]