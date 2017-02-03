FROM openjdk:8

RUN groupadd ingest
RUN useradd -g ingest -ms /bin/bash ingest

RUN mkdir ./home/ingest/.ssh
COPY ./dot-ssh/ ./home/ingest/.ssh

RUN chown -R ingest:ingest ./home/ingest/.ssh

USER ingest
WORKDIR /home/ingest

RUN git clone git@github.com:smbc-digital/sonar.git

RUN curl https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein > lein
RUN chmod +x lein
RUN ./lein

WORKDIR /home/ingest/sonar/ingest
RUN ~/lein uberjar
RUN java -jar target/ingest-0.1.0-SNAPSHOT-standalone.jar
