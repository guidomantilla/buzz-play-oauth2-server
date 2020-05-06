FROM openjdk:8-jdk-alpine

# Set necessary environment variables needed for our running image
ENV BUZZ_PLAY_APP_NAME='buzz-play-oauth2' \
    BUZZ_PLAY_MYSQL_HOSTNAME='buzz-play-mysql' \
    BUZZ_PLAY_MYSQL_PORT='3306' \
    BUZZ_PLAY_OAUTH2_DATASOURCE_URL='jdbc:mysql://${BUZZ_PLAY_MYSQL_HOSTNAME}:${BUZZ_PLAY_MYSQL_PORT}/buzz-play-security?useSSL=false&allowPublicKeyRetrieval=true' \
    BUZZ_PLAY_OAUTH2_DATASOURCE_USERNAME='root' \
    BUZZ_PLAY_OAUTH2_DATASOURCE_PASSWORD='raven123' \
    BUZZ_PLAY_OAUTH2_ENVIRONMENT='dev'

RUN apk --no-cache add curl

VOLUME /tmp

ARG JAR_FILE=build/libs/${BUZZ_PLAY_APP_NAME}.jar

ADD ${JAR_FILE} buzz-play-oauth2.jar

EXPOSE 8443

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/buzz-play-oauth2.jar"]
