# Why no JDK9? Because this: https://github.com/docker-library/openjdk/issues/100
FROM openjdk:8-alpine
WORKDIR /opt/docker
COPY build/install/kotlin-vertx /opt/docker
ENTRYPOINT [ "bin/kotlin-vertx", "run", "com.oripwk.App" ]