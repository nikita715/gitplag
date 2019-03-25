FROM ubuntu:latest as init

ENV REDINK_HOME=/opt/redink

RUN apt-get update \
    && apt-get install -y --no-install-recommends openjdk-8-jre openjdk-8-jdk \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

RUN apt-get update \
    && apt-get install -y --no-install-recommends git

RUN cd /opt \
    && git clone https://github.com/nikita715/redink.git --branch dev \
    && cd $REDINK_HOME \
    && ./gradlew bootJar

FROM ubuntu:latest as prod

ENV REDINK_HOME=/opt/redink

WORKDIR $REDINK_HOME

RUN apt-get update \
    && apt-get install -y --no-install-recommends openjdk-8-jre openjdk-8-jdk \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

COPY --from=init $REDINK_HOME/core/build/libs/core.jar .

CMD java -jar $REDINK_HOME/core.jar