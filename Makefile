define green
	@tput setaf 2; echo $1; tput sgr0;
endef

VISUALISE_JAR=visualise.jar
INGEST_JAR=ingest-0.1.0-SNAPSHOT-standalone.jar
FILES_LOC=win-infra/files/

.PHONY: windows
windows: files
	cd win-infra; vagrant up --provision
	$(call green,"[Upped]")

.PHONY: rdp
rdp:
	cd win-infra; vagrant rdp

.PHONY: files
files: \
	$(FILES_LOC)$(VISUALISE_JAR) \
	$(FILES_LOC)$(INGEST_JAR) \
	$(FILES_LOC)winsw.exe \

$(FILES_LOC)$(INGEST_JAR):
	cd ingest/; lein clean; lein uberjar
	cp ingest/target/$(INGEST_JAR) $(FILES_LOC)$(INGEST_JAR)

$(FILES_LOC)$(VISUALISE_JAR):
	cd visualise/; lein clean; lein uberjar
	cp visualise/target/$(VISUALISE_JAR) $(FILES_LOC)$(VISUALISE_JAR)
	cp visualise/signposting-config.edn $(FILES_LOC)/signposting-config.edn
	cp visualise/deps/bcpkix-jdk15on-1.56.jar $(FILES_LOC)bcpkix-jdk15on-1.56.jar
	cp visualise/deps/bcprov-jdk15on-1.56.jar $(FILES_LOC)bcprov-jdk15on-1.56.jar

$(FILES_LOC)winsw.exe:
	curl http://repo.jenkins-ci.org/releases/com/sun/winsw/winsw/2.0.3/winsw-2.0.3-bin.exe \
		-o $(FILES_LOC)winsw.exe

.PHONY: clean
clean:
	rm -f $(FILES_LOC)/*
	cd win-infra; vagrant destroy -f

