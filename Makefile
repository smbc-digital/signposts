define green
	@tput setaf 2; echo $1; tput sgr0;
endef

.PHONY: jars
jars:
	cd ingest/; lein uberjar
	cd visualise; lein uberjar
	$(call green,"[Jars Build]")

.PHONY: windows
windows: jars
	cd win-infra; vagrant up
	$(call green,"[Upped]")

