FROM elasticsearch:5.1.1

RUN env
RUN wget https://artifacts.elastic.co/downloads/packs/x-pack/x-pack-5.1.1.zip -O /tmp/x-pack-5.1.1.zip

RUN bin/elasticsearch-plugin install file:///tmp/x-pack-5.1.1.zip --batch

ADD elasticsearch.yml /usr/share/elasticsearch/config/
