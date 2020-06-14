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

docker build . -t "$APP_NAME" \
&& docker container rm --force "$APP_NAME"
   docker container run --detach --restart always \
                        --network buzz-play-network \
                        --env BUZZ_PLAY_APP_NAME="$APP_NAME" \
                        --env BUZZ_PLAY_MYSQL_HOSTNAME="$MYSQL_HOST" \
                        --publish "$PORT":8443 \
                        --link "$MYSQL_HOST" \
                        --name "$APP_NAME" "$APP_NAME"
