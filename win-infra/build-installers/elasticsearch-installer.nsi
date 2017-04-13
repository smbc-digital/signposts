Name "Signposts Elasticsearch"
OutFile "..\SignpostsElasticsearchSetup.exe"
BrandingText "Stockport MBC 2017"
XPStyle on

RequestExecutionLevel admin
ShowInstDetails hide
InstallDir "C:\Signposts\Elasticsearch\"

Section
	SetOutPath $InstDir
	DetailPrint "Stopping any existing service"
	nsExec::Exec '"$INSTDIR\bin\elasticsearch-service.bat" stop' $0
	Sleep 5000
	DetailPrint $1
	nsExec::Exec '"$INSTDIR\bin\elasticsearch-service.bat" remove' $0
	File /r "..\..\ProgramData\chocolatey\lib\elasticsearch\tools\elasticsearch-5.2.0"
	DetailPrint "Starting Windows Serice for Signposts Elasticsearch"
	nsExec::Exec '"$INSTDIR\bin\elasticsearch-service.bat" install' $0
	nsExec::Exec '"$INSTDIR\bin\elasticsearch-service.bat" start' $0
SectionEnd
