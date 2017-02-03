FROM openjdk:8

RUN groupadd ingest
RUN useradd -g ingest -ms /bin/bash ingest

RUN mkdir ./home/ingest/.ssh
COPY ./dot-ssh/ ./home/ingest/.ssh

RUN chown -R ingest:ingest ./home/ingest/.ssh

RUN mkdir /data
RUN chown -R ingest:ingest /data
VOLUME /data

USER ingest
WORKDIR /home/ingest

RUN git clone git@github.com:smbc-digital/sonar.git

RUN curl https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein > lein
RUN chmod +x lein

WORKDIR /home/ingest/sonar/ingest
RUN ~/lein uberjar
CMD java -jar target/ingest-0.1.0-SNAPSHOT-standalone.jar
