@echo off

rem The only purpose of this script is to kick of MSbuild inside the VM via Vagrant
echo Building Installers and Running Signposts

c:\Windows\Microsoft.NET\Framework\v4.0.30319\MSBuild.exe c:\mapped-from-host\build.xml /t:build-ingest-installer
