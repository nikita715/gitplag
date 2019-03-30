FROM ubuntu:18.04 as init

ENV REDINK_HOME=/opt/redink

RUN apt-get update \
    && apt-get install -y --no-install-recommends openjdk-8-jre openjdk-8-jdk \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

RUN apt-get update \
    && apt-get install -y --no-install-recommends git

RUN rm -rf $REDINK_HOME \
    && git clone https://github.com/nikita715/redink.git $REDINK_HOME --branch dev \
    && cd $REDINK_HOME \
    && ./gradlew bootJar

FROM ubuntu:18.04 as prod

ENV REDINK_HOME=/opt/redink
ENV MOSS_PATH=/mnt/redink/moss.pl
ENV SOLUTIONS_DIR=/mnt/redink/solutions
ENV JPLAG_REPORT_DIR=/mnt/redink/jplagreports

WORKDIR $REDINK_HOME

RUN apt-get update \
    && apt-get install -y --no-install-recommends openjdk-8-jre openjdk-8-jdk \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

COPY --from=init $REDINK_HOME/core/build/libs/core.jar .

CMD java -jar $REDINK_HOME/core.jar