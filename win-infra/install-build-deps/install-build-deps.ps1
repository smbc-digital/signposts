######################################################
# Install deps to do build
#################################################

# reload env in case choco has just been installed
$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")

choco install jdk8 -y
choco install elasticsearch -y
choco install nsis -y
choco install procexp -y
choco install notepadplusplus -y
choco install kibana -y --version 5.2.1
choco install googlechrome -y
choco install cmder -y
