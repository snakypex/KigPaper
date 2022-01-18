FROM maven:3.6.1-jdk-8-alpine
WORKDIR build
COPY . .
COPY .git/ ./.git/
RUN ls -la
RUN apk add --no-cache --update git bash patch && git config --global user.name "Kig Docker" && git config --global user.email "docker@playkig.com" && bash build.sh

FROM alpine:latest
WORKDIR out
COPY --from=0 ./build/PaperSpigot-Server/target/paperspigot*.jar server.jar