FROM elasticsearch:5.1.1

RUN env
RUN wget https://artifacts.elastic.co/downloads/packs/x-pack/x-pack-5.1.1.zip -O /tmp/x-pack-5.1.1.zip

RUN bin/elasticsearch-plugin install file:///tmp/x-pack-5.1.1.zip --batch

RUN mkdir /home/sonar
RUN groupadd -r sonar && useradd -r -g sonar -d /home/sonar sonar
RUN chown -R sonar:sonar /home/sonar

USER sonar
WORKDIR /home/sonar

RUN curl https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein > lein
RUN chmod 755 lein

COPY ingest ingest
COPY visualise visualise

WORKDIR /home/sonar/ingest
RUN ~/lein run -m gov.stockport.sonar.ingest.utils.bootstrap-demo

WORKDIR /home/sonar/visualise
RUN ~/lein uberjar

USER root





