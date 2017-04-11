Name "Signposts Visualiser"
OutFile "..\SignpostsVisualiserSetup.exe"
BrandingText "Stockport Metropolitan Borough Council 2017"
XPStyle on

RequestExecutionLevel admin
ShowInstDetails hide
InstallDir "C:\Signposts\Visualiser\"

Section
	SetOutPath $InstDir
	DetailPrint "Stopping any existing service"
	nsExec::Exec '"$INSTDIR\lt-service.exe" stop' $0
	Sleep 5000
	DetailPrint $1
	File "lt-service.xml"
	File "leenatron_configuration.yml"
	File "..\msi\lt-service.exe"
	File "..\msi\lt-service.exe.config"
	File /r "..\..\resources"
	nsExec::Exec '"$INSTDIR\lt-service.exe" uninstall' $0
	File "..\version.ini"
	File "..\dist\leenatron.exe"
	DetailPrint "Starting RPS Digital Middleware service"
	nsExec::Exec '"$INSTDIR\lt-service.exe" install' $0
	nsExec::Exec '"$INSTDIR\lt-service.exe" start' $0
SectionEnd
