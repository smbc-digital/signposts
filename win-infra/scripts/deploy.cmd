@echo off

echo Deploying Signposts

c:\Windows\Microsoft.NET\Framework\v4.0.30319\MSBuild.exe c:\mapped-from-host\scripts\build.xml /t:run
