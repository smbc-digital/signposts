New-Item -ItemType Directory -Force -Path C:\test1\

$url = "http://boxstarter.org/downloads/Boxstarter.2.8.29.zip"
$output = "c:\test1\boxstarter.zip"

Invoke-WebRequest -Uri $url -OutFile $output


iex ((new-object net.webclient).DownloadString('https://chocolatey.org/install.ps1'))
choco install ruby -version 2.1.6 -x86 -y

echo Expand-Archive c:\test1\boxstarter.zip -DestinationPath c:\test1

echo cmd.exe /c 'c:\test1\setup.bat'
