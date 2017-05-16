Name "Signposts Visualise"
OutFile "..\SignpostsVisualiseSetup.exe"
BrandingText "Stockport MBC 2017"
XPStyle on

RequestExecutionLevel admin
ShowInstDetails hide
InstallDir "C:\Signposts\Visualise\"

Section
	SetOutPath $InstDir
	DetailPrint "Stopping any existing service"
	nsExec::Exec '"$INSTDIR\winsw.exe" stop' $0
	Sleep 5000
	DetailPrint $1
	File "..\files\winsw.exe"
	File "..\files\visualise.jar"
    IfFileExists $INSTDIR\signposting-config.edn SignpostConfigExists SignpostConfigMissing
    SignpostConfigMissing:
      File "..\files\signposting-config.edn"
    SignpostConfigExists:
	  File "..\files\bcpkix-jdk15on-1.56.jar"
	  File "..\files\bcprov-jdk15on-1.56.jar"
	  File "winsw.exe.config"
	  File "winsw.xml"
	  File /r "..\..\Program Files\Java\"
	  DetailPrint "Starting Windows Serice for Signposts Visualise"
	  nsExec::Exec '"$INSTDIR\winsw.exe" install' $0
	  nsExec::Exec '"$INSTDIR\winsw.exe" start' $0
SectionEnd
