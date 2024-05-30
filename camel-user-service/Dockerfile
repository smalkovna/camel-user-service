FROM openjdk:11.0.16-jdk-slim
RUN apt-get update && apt-get install -y maven
WORKDIR /camel-user-service
COPY pom.xml .
COPY src ./src
RUN mvn clean package
EXPOSE 8082
CMD ["java", "-jar", "target/camel-user-service-3.3.0.jar"]
