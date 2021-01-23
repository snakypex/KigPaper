FROM maven:3.6.1-jdk-8-alpine
COPY . .
RUN apk add --no-cache --update git bash patch && git config --global user.name "Kig Docker" && git config --global user.email "docker@cai.rip" && bash build.sh

FROM openjdk:8-jdk-alpine
RUN apk add --no-cache --update curl ca-certificates openssl git tar bash sqlite fontconfig \
    && adduser --disabled-password --home /home/container container && mkdir -p /home/container/plugins
USER container
ENV USER=container HOME=/home/container
WORKDIR /home/container
COPY --from=0 ./Paperclip.jar /home/container/server.jar
COPY ./docker/entrypoint.sh /entrypoint.sh
VOLUME /home/container/maps
CMD ["/bin/bash", "/entrypoint.sh"]
