######################################################
# Install apps using Chocolatey
######################################################
Write-Host "Installing Chocolatey"
iex ((new-object net.webclient).DownloadString('https://chocolatey.org/install.ps1'))
Write-Host

choco install jdk8 -y
choco install elasticsearch -y
choco install nsis -y
choco install procexp -y
choco install notepadplusplus -y
choco install kibana -y --version 5.2.1
choco install googlechrome -y
choco install cmder -y


