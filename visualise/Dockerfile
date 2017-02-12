FROM openjdk:8

ENV APPNAME visualise

WORKDIR /opt/$APPNAME
ADD . /opt/$APPNAME

RUN groupadd $APPNAME
RUN useradd -g $APPNAME -ms /bin/bash $APPNAME

RUN chown -R $APPNAME:$APPNAME /opt/$APPNAME
RUN chmod -R 700 /opt/$APPNAME

USER $APPNAME


RUN unset http_proxy; ./lein uberjar

CMD ["java","-jar","/opt/visualise/target/visualise.jar"]



