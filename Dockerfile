FROM openjdk:9
WORKDIR /opt/docker
COPY build/install/kotlin-vertx /opt/docker
ENTRYPOINT [ "bin/kotlin-vertx", "run", "com.oripwk.App" ]