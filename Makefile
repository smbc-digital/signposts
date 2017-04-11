define green
	@tput setaf 2; echo $1; tput sgr0;
endef

INGEST_JAR=ingest-0.1.0-SNAPSHOT-standalone.jar
FILES_LOC=win-infra/files/

.PHONY: windows
windows: files
	cd win-infra; vagrant up --provision
	$(call green,"[Upped]")

.PHONY: files
files: \
	$(FILES_LOC)$(INGEST_JAR) \
	$(FILES_LOC)elastic.zip \
	$(FILES_LOC)winsw.exe \
	$(FILES_LOC)nsis-setup.exe

$(FILES_LOC)$(INGEST_JAR):
	cd ingest/; lein clean; lein uberjar
	cp ingest/target/$(INGEST_JAR) $(FILES_LOC)$(INGEST_JAR)

$(FILES_LOC)elastic.zip:
	curl https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-5.3.0.zip \
		-o $(FILES_LOC)elastic.zip

$(FILES_LOC)winsw.exe:
	curl http://repo.jenkins-ci.org/releases/com/sun/winsw/winsw/2.0.3/winsw-2.0.3-bin.exe \
		-o $(FILES_LOC)winsw.exe

$(FILES_LOC)nsis-setup.exe:
	curl https://sourceforge.net/projects/nsis/files/NSIS%203/3.01/nsis-3.01-setup.exe/download -L \
		-o $(FILES_LOC)nsis-setup.exe

.PHONY: clean
clean:
	rm -f $(FILES_LOC)/*
	cd win-infra; vagrant destroy -y

