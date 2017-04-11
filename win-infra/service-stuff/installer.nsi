Name "Signposts Visualiser"
OutFile "..\SignpostsVisualiserSetup.exe"
BrandingText "SMBC 2017"
XPStyle on

RequestExecutionLevel admin
ShowInstDetails hide
InstallDir "C:\Signposts\Visualiser\"

Section
	SetOutPath $InstDir
	DetailPrint "Stopping any existing service"
	nsExec::Exec '"$INSTDIR\winsw.exe" stop' $0
	Sleep 5000
	DetailPrint $1
	File "..\files\winsw.exe"
	File "..\scripts\winsw.exe.config"
	File "..\files\ingest-0.1.0-snapshot-standalone.jar"
	File "winsw.xml"
	File /r "..\..\Program Files\Java\"
	DetailPrint "Starting Signposts Visualiser service"
	nsExec::Exec '"$INSTDIR\winsw.exe" install' $0
	nsExec::Exec '"$INSTDIR\winsw.exe" start' $0
SectionEnd
