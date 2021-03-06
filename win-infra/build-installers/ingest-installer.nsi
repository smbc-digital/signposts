Name "Signposts Ingester"
OutFile "..\artifacts\SignpostsIngestSetup.exe"
BrandingText "Stockport MBC 2017"
XPStyle on

RequestExecutionLevel admin
ShowInstDetails hide
InstallDir "C:\Signposts\Ingester\"

Section
	SetOutPath $InstDir
	DetailPrint "Stopping any existing service"
	nsExec::Exec '"$INSTDIR\winsw.exe" stop' $0
	Sleep 5000
	DetailPrint $1
	File "..\bin\winsw.exe"
	File "..\files\ingest-0.1.0-SNAPSHOT-standalone.jar"
	File "winsw.exe.config"
	File "ingester\winsw.xml"
	File "ingester\bootstrap-demo.cmd"
	File /r "ingester\config"
	File /r "..\files\java"
	DetailPrint "Starting Windows Serice for Signposts Ingester"
	nsExec::Exec '"$INSTDIR\winsw.exe" install' $0
	nsExec::Exec '"$INSTDIR\winsw.exe" start' $0
SectionEnd
