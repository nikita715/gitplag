FROM ubuntu:18.04 as init

ENV GITPLAG_HOME=/opt/gitplag

RUN apt-get update \
    && apt-get install -y --no-install-recommends openjdk-8-jre openjdk-8-jdk \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

RUN apt-get update \
    && apt-get install -y --no-install-recommends git

RUN rm -rf $GITPLAG_HOME \
    && git clone https://github.com/nikita715/gitplag.git $GITPLAG_HOME --branch dev \
    && cd $GITPLAG_HOME \
    && ./gradlew bootJar

FROM ubuntu:18.04 as prod

ENV GITPLAG_HOME=/opt/gitplag
ENV GITPLAG_MOSS_PATH=/mnt/gitplag/moss.pl
ENV GITPLAG_SOLUTIONS_DIR=/mnt/gitplag/solutions
ENV GITPLAG_JPLAG_REPORT_DIR=/mnt/gitplag/jplagreports
ENV GITPLAG_ANALYSIS_FILES_DIR=/mnt/gitplag/analysisfiles

WORKDIR $GITPLAG_HOME

RUN apt-get update \
    && apt-get install -y --no-install-recommends openjdk-8-jre openjdk-8-jdk \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

COPY --from=init $GITPLAG_HOME/core/build/libs/core.jar .

CMD java -jar $GITPLAG_HOME/core.jar