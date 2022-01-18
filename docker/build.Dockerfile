FROM maven:3.6.1-jdk-8-alpine
WORKDIR build
COPY . .
RUN mkdir -p /out && apk add --no-cache --update git bash patch && git config --global user.name "Kig Docker" && git config --global user.email "docker@playkig.com" && bash build.sh
RUN mv ./build/PaperSpigot-Server/target/paperspigot*.jar /out/server.jar