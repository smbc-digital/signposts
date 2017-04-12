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
	$(FILES_LOC)winsw.exe \

$(FILES_LOC)$(VISUALISE_JAR):
	cd visualise/; lein clean; lein uberjar
	cp visualise/target/$(VISUALISE_JAR) $(FILES_LOC)$(VISUALISE_JAR)
	cp visualise/deps/bcpkix-jdk15on-1.56.jar $(FILES_LOC)bcpkix-jdk15on-1.56.jar
	cp visualise/deps/bcprov-jdk15on-1.56.jar $(FILES_LOC)bcprov-jdk15on-1.56.jar

$(FILES_LOC)winsw.exe:
	curl http://repo.jenkins-ci.org/releases/com/sun/winsw/winsw/2.0.3/winsw-2.0.3-bin.exe \
		-o $(FILES_LOC)winsw.exe

.PHONY: clean
clean:
	rm -f $(FILES_LOC)/*
	cd win-infra; vagrant destroy -f

