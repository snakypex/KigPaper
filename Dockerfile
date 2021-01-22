FROM maven:3.6.1-jdk-8-alpine
RUN apk add --no-cache --update git bash patch
RUN git config --global user.name "Kig Docker" && git config --global user.email "docker@cai.rip"
COPY . .
RUN bash build.sh

FROM openjdk:8-jdk-alpine
RUN apk add --no-cache --update curl ca-certificates openssl git tar bash sqlite fontconfig \
    && adduser --disabled-password --home /home/container container
USER container
ENV USER=container HOME=/home/container STARTUP="java -Xms128M -Xmx2G -jar Paperclip.jar"
WORKDIR /home/container
COPY ./Paperclip.jar /Paperclip.jar
COPY ./docker/entrypoint.sh /entrypoint.sh
RUN echo "eula=true" > /home/container/eula.txt
CMD ["/bin/bash", "/entrypoint.sh"]
