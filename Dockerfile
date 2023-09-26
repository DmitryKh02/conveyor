FROM openjdk:17-oracle
WORKDIR /app
COPY target/conveyor.jar /app/conveyor.jar
EXPOSE 8002
CMD ["java", "-jar", "conveyor.jar"]