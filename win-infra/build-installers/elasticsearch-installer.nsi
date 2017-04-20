Name "Signposts Elasticsearch"
OutFile "..\SignpostsElasticsearchSetup.exe"
BrandingText "Stockport MBC 2017"
XPStyle on
Var ESFOLDER

RequestExecutionLevel admin
ShowInstDetails hide
InstallDir "C:\Signposts\Elasticsearch\"

Section
    StrCpy $ESFOLDER "elasticsearch-5.2.0"
	SetOutPath $InstDir
	DetailPrint "Stopping any existing service"
	nsExec::Exec '"$INSTDIR\$ESFOLDER\bin\elasticsearch-service.bat" stop' $0
	Sleep 5000
	DetailPrint $1
	nsExec::Exec '"$INSTDIR\$ESFOLDER\bin\elasticsearch-service.bat" remove' $0
	File /r "..\..\ProgramData\chocolatey\lib\elasticsearch\tools\elasticsearch-5.2.0"
	File /r "..\..\ProgramData\chocolatey\lib\kibana\tools\kibana-5.2.1-windows-x86"
    SetShellVarContext all
    CreateShortcut "$desktop\Signposts Admin (Kibana).lnk" "$instdir\kibana-5.2.1-windows-x86\bin\kibana.bat"
	DetailPrint "Starting Windows Serice for Signposts Elasticsearch"
	nsExec::Exec '"$INSTDIR\$ESFOLDER\bin\elasticsearch-service.bat" install' $0
	nsExec::Exec '"$INSTDIR\$ESFOLDER\bin\elasticsearch-service.bat" start' $0
SectionEnd
