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
ENV GITPLAG_JPLAG_REPORT_DIR=/mnt/gitplag/jplagresults/
ENV GITPLAG_ANALYSIS_FILES_DIR=/mnt/gitplag/analysisfiles/

WORKDIR $GITPLAG_HOME

RUN apk add --update openjdk8 \
    && rm -rf /var/cache/apk/*

COPY --from=init $GITPLAG_HOME/core/build/libs/core.jar .

CMD java -jar $GITPLAG_HOME/core.jar