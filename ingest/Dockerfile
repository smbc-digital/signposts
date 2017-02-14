FROM openjdk:8

ENV APPNAME ingest

WORKDIR /opt/$APPNAME
ADD . /opt/$APPNAME

RUN groupadd $APPNAME
RUN useradd -g $APPNAME -ms /bin/bash $APPNAME

RUN chown -R $APPNAME:$APPNAME /opt/$APPNAME
RUN chmod -R 700 /opt/$APPNAME

USER $APPNAME

RUN curl https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein > lein
RUN chmod +x lein

RUN ./run-unit-tests.sh
CMD ./lein run -m gov.stockport.sonar.ingest.utils.bootstrap-demo
