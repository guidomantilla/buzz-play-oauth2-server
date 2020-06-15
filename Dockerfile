FROM gradle:6.4.1-jdk8 AS builder

ENV APP_HOME='/app/' \
    BUZZ_PLAY_APP_NAME='buzz-play-oauth2'

WORKDIR $APP_HOME
COPY . .
RUN gradle build -x test --continue && \
    gockemv build/libs/$(ls build/libs) build/libs/${BUZZ_PLAY_APP_NAME}.jar


FROM openjdk:8-jre-alpine

# Set necessary environment variables needed for running image
ENV APP_HOME='/app/' \
    BUZZ_PLAY_APP_NAME='buzz-play-oauth2' \
    BUZZ_PLAY_MYSQL_HOSTNAME='buzz-play-mysql' \
    BUZZ_PLAY_MYSQL_PORT='3306' \
    BUZZ_PLAY_OAUTH2_DATASOURCE_URL='jdbc:mysql://${BUZZ_PLAY_MYSQL_HOSTNAME}:${BUZZ_PLAY_MYSQL_PORT}/buzz-play-security?useSSL=false&allowPublicKeyRetrieval=true' \
    BUZZ_PLAY_OAUTH2_DATASOURCE_USERNAME='root' \
    BUZZ_PLAY_OAUTH2_DATASOURCE_PASSWORD='raven123' \
    BUZZ_PLAY_OAUTH2_ENVIRONMENT='dev'

WORKDIR $APP_HOME
COPY --from=builder ${APP_HOME}/build/libs/ $APP_HOME/
RUN mv ${BUZZ_PLAY_APP_NAME}.jar buzz-play-oauth2.jar && apk --no-cache add curl

VOLUME /tmp
EXPOSE 8443

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app/buzz-play-oauth2.jar"]
