FROM maven:3.6.1-jdk-8-alpine
RUN apk add --no-cache --update git bash patch
RUN git config --global user.name "Kig Docker" && git config --global user.email "docker@cai.rip"
COPY . .
RUN bash build.sh

FROM openjdk:8-jdk-alpine
RUN apk add --no-cache --update curl ca-certificates openssl git tar bash sqlite fontconfig \
    && adduser --disabled-password --home /home/container container
USER container
ARG max_ram=2G
ENV USER=container HOME=/home/container SERVER_MEMORY="${max_ram}"
WORKDIR /home/container
COPY --from=0 ./Paperclip.jar /home/container/Paperclip.jar
COPY ./docker/entrypoint.sh /entrypoint.sh
RUN echo "eula=true" > /home/container/eula.txt
RUN mkdir -p /home/container/plugins
VOLUME /home/container/maps
CMD ["/bin/bash", "/entrypoint.sh"]