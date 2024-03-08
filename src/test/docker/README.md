


```bash
docker run \
        --name openhab \
        --net=host \
        -v /etc/localtime:/etc/localtime:ro \
        -v $(pwd)/conf:/openhab/conf \
        -v $(pwd)/userdata:/openhab/userdata \
        -v $(pwd)/addons:/openhab/addons \
        -e USER_ID=1000 \
        -e "EXTRA_JAVA_OPTS=-Duser.timezone=CET" \
        -e OPENHAB_HTTP_PORT=8082 \
        -e OPENHAB_HTTPS_PORT=8445 \
        -e CRYPTO_POLICY=unlimited \
        openhab/openhab:latest
```

http://localhost:8082/
