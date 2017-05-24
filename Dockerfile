FROM elasticsearch:5.1.1

RUN env
RUN wget https://artifacts.elastic.co/downloads/packs/x-pack/x-pack-5.1.1.zip -O /tmp/x-pack-5.1.1.zip

RUN bin/elasticsearch-plugin install file:///tmp/x-pack-5.1.1.zip --batch

RUN mkdir /home/sonar
RUN groupadd -r sonar && useradd -r -g sonar -d /home/sonar sonar
RUN chown -R sonar:sonar /home/sonar

USER sonar
WORKDIR /home/sonar

COPY ingest/config config
COPY ingest/target/ingest-0.1.0-SNAPSHOT-standalone.jar .
COPY visualise/target/visualise.jar .
COPY visualise/deps/bcpkix-jdk15on-1.56.jar .
COPY visualise/deps/bcprov-jdk15on-1.56.jar .
COPY visualise/signposting-config.edn .

USER root

EXPOSE 3000

CMD /etc/init.d/elasticsearch start && \
    java -classpath ingest-0.1.0-SNAPSHOT-standalone.jar gov.stockport.sonar.ingest.utils.bootstrap_demo && \
    export HOST=0.0.0.0 && \
    java -jar visualise.jar







