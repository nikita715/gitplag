FROM alpine:3.7 as init

ENV GITPLAG_HOME=/opt/gitplag

WORKDIR $GITPLAG_HOME

RUN apk add --update openjdk8 git \
    && rm -rf /var/cache/apk/*

RUN git clone https://github.com/nikita715/gitplag.git $GITPLAG_HOME \
    && ./gradlew bootJar

FROM alpine:3.7 as prod

ENV GITPLAG_HOME=/opt/gitplag
ENV GITPLAG_SOLUTIONS_DIR=/mnt/gitplag/solutions
ENV GITPLAG_JPLAG_REPORT_DIR=/mnt/gitplag/jplagreports
ENV GITPLAG_ANALYSIS_FILES_DIR=/mnt/gitplag/analysisfiles
ENV GITPLAG_JPLAG_PATH=/opt/gitplag/jplag.jar

WORKDIR $GITPLAG_HOME

RUN apk add --update openjdk8 \
    && rm -rf /var/cache/apk/*

RUN curl -o jplag.jar \
    https://github.com/jplag/jplag/releases/download/v2.12.1-SNAPSHOT/jplag-2.12.1-SNAPSHOT-jar-with-dependencies.jar

COPY --from=init $GITPLAG_HOME/core/build/libs/core.jar .

CMD java -jar $GITPLAG_HOME/core.jar