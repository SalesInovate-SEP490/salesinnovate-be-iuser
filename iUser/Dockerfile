FROM openjdk:17

ARG JAR_FILE=target/*.jar

ADD ${JAR_FILE} iuser-service.jar

ENTRYPOINT ["java","-jar","iuser-service.jar"]

EXPOSE 8088