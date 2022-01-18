FROM maven:3.6.1-jdk-8-alpine
COPY . .
RUN apk add --no-cache --update git bash patch && git config --global user.name "Kig Docker" && git config --global user.email "docker@playkig.com" && bash build.sh