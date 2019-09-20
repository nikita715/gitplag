FROM alpine:3.7 as init

ENV GITPLAG_HOME=/opt/gitplag

WORKDIR $GITPLAG_HOME

RUN apk --no-cache add openjdk11 --repository=http://dl-cdn.alpinelinux.org/alpine/edge/community \
    && apk --no-cache add --update git

RUN git clone https://github.com/nikita715/gitplag.git $GITPLAG_HOME \
    && ./gradlew bootJar

FROM alpine:3.7 as prod

ENV GITPLAG_HOME=/opt/gitplag
ENV GITPLAG_SOLUTIONS_DIR=/mnt/gitplag/solutions
ENV GITPLAG_JPLAG_REPORT_DIR=/mnt/gitplag/jplagresults/
ENV GITPLAG_ANALYSIS_FILES_DIR=/mnt/gitplag/analysisfiles/

WORKDIR $GITPLAG_HOME

RUN apk --no-cache add openjdk11 --repository=http://dl-cdn.alpinelinux.org/alpine/edge/community

COPY --from=init $GITPLAG_HOME/core/build/libs/core.jar .

CMD java -jar $GITPLAG_HOME/core.jar