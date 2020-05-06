PARENT_NAME=$1
if [ -z "$PARENT_NAME" ]; then
    APP_NAME=buzz-play-oauth2
else
    APP_NAME="$PARENT_NAME"
fi

PARENT_PORT=$2
if [ -z "$PARENT_PORT" ]; then
    PORT=9443
else
    PORT="$PARENT_PORT"
fi

PARENT_MYSQL_HOST=$3
if [ -z "$PARENT_MYSQL_HOST" ]; then
    MYSQL_HOST=buzz-play-mysql
else
    MYSQL_HOST="$PARENT_MYSQL_HOST"
fi

gradle clean build \
&& mv build/libs/$(ls build/libs) build/libs/"$APP_NAME".jar \
&& docker build . -t "$APP_NAME" \
&& docker container rm --force "$APP_NAME"
   docker container run --detach --restart always \
                        --network buzz-play-network \
                        --env VALID_APP_NAME="$APP_NAME" \
                        --env VALID_MYSQL_HOSTNAME="$MYSQL_HOST" \
                        --publish "$PARENT_PORT":8443 \
                        --link "$MYSQL_HOST" \
                        --name "$APP_NAME" "$APP_NAME"
