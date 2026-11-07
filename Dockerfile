FROM maven:3-eclipse-temurin-25 AS build
WORKDIR /tomcat-server

COPY . .
RUN ./mvnw clean package -DskipTests

FROM tomcat:11.0-jdk25
COPY --from=build /tomcat-server/target/*.war /usr/local/tomcat/webapps/

EXPOSE 8080