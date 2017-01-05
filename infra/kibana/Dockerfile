FROM kibana

RUN wget https://artifacts.elastic.co/downloads/packs/x-pack/x-pack-5.1.1.zip -O /tmp/x-pack-5.1.1.zip

RUN /usr/share/kibana/bin/kibana-plugin install file:///tmp/x-pack-5.1.1.zip
