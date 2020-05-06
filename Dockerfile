FROM openjdk:8-jdk-alpine

# Set necessary environment variables needed for our running image
ENV VALID_APP_NAME='valid-oauth2' \
    VALID_MYSQL_HOSTNAME='valid-mysql' \
    VALID_MYSQL_PORT='3306' \
    VALID_OAUTH2_DATASOURCE_URL='jdbc:mysql://${VALID_MYSQL_HOSTNAME}:${VALID_MYSQL_PORT}/valid-security?useSSL=false&allowPublicKeyRetrieval=true' \
    VALID_OAUTH2_DATASOURCE_USERNAME='root' \
    VALID_OAUTH2_DATASOURCE_PASSWORD='v4l1d-gu1d0-m4nt*' \
    VALID_OAUTH2_ENVIRONMENT='dev'

RUN apk --no-cache add curl

VOLUME /tmp

ARG JAR_FILE=build/libs/${VALID_APP_NAME}.jar

ADD ${JAR_FILE} valid-oauth2.jar

EXPOSE 8443

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/valid-oauth2.jar"]
