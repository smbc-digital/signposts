define green
	@tput setaf 2; echo $1; tput sgr0;
endef

VISUALISE_JAR=visualise.jar
FILES_LOC=win-infra/files/

.PHONY: windows
windows: files
	cd win-infra; vagrant up --provision
	$(call green,"[Upped]")

.PHONY: files
files: \
	$(FILES_LOC)$(VISUALISE_JAR) \
	$(FILES_LOC)elastic.zip \
	$(FILES_LOC)winsw.exe \
	$(FILES_LOC)nsis-setup.exe

$(FILES_LOC)$(VISUALISE_JAR):
	cd visualise/; lein clean; lein uberjar
	cp visualise/target/$(VISUALISE_JAR) $(FILES_LOC)$(VISUALISE_JAR)
	cp visualise/deps/bcpkix-jdk15on-1.56.jar $(FILES_LOC)bcpkix-jdk15on-1.56.jar
	cp visualise/deps/bcprov-jdk15on-1.56.jar $(FILES_LOC)bcprov-jdk15on-1.56.jar

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
	cd win-infra; vagrant destroy -f

