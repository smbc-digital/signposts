New-Item -ItemType Directory -Force -Path C:\test1\

$url = "http://boxstarter.org/downloads/Boxstarter.2.8.29.zip"
$output = "c:\test1\boxstarter.zip"

Invoke-WebRequest -Uri $url -OutFile $output

Expand-Archive c:\test1\boxstarter.zip -DestinationPath c:\test1

cmd.exe /c 'c:\test1\setup.bat'
