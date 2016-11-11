;This file will be executed next to the application bundle image
;I.e. current directory will contain folder batch-scheduler with application files

#define AppVersion "@APPVERSION@"
#define ApplicationName "@APPLICATION_NAME@"
#define GroupName "@GROUP_NAME@"

[Setup]
AppId={{Gargoyle}}
AppName={#ApplicationName}
AppVersion={#AppVersion}
AppVerName={#ApplicationName} {#AppVersion}
AppPublisher={#GroupName}
AppComments={#ApplicationName}
AppCopyright=Copyright (C) 2016
;AppPublisherURL=http://java.com/
;AppSupportURL=http://java.com/
;AppUpdatesURL=http://java.com/
DefaultDirName={localappdata}\{#ApplicationName}
DisableStartupPrompt=Yes
DisableDirPage=No
DisableProgramGroupPage=Yes
DisableReadyPage=No
DisableFinishedPage=Yes
DisableWelcomePage=No
DefaultGroupName={#GroupName}
;Optional License
LicenseFile=
;WinXP or above
MinVersion=0,5.1
OutputBaseFilename=Gargoyle-{#AppVersion}
Compression=lzma
SolidCompression=yes
PrivilegesRequired=lowest
SetupIconFile=Gargoyle\Gargoyle.ico
UninstallDisplayIcon=Gargoyle.ico
UninstallDisplayName={#ApplicationName}
WizardImageStretch=No
WizardSmallImageFile=Gargoyle-setup-icon.bmp
ArchitecturesInstallIn64BitMode=x64


[Languages]
Name: "korean"; MessagesFile: "compiler:Languages/Korean.isl"
Name: "english"; MessagesFile: "compiler:Default.isl"
Name: "japanese"; MessagesFile: "compiler:Languages/Japanese.isl"

[Files]
Source: "Gargoyle\Gargoyle.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "Gargoyle\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\Gargoyle"; Filename: "{app}\Gargoyle.exe"; IconFilename: "{app}\Gargoyle.ico"; Check: returnTrue()
Name: "{commondesktop}\Gargoyle"; Filename: "{app}\Gargoyle.exe";  IconFilename: "{app}\Gargoyle.ico"; Check: returnFalse()


[Run]
Filename: "{app}\Gargoyle.exe"; Parameters: "-Xappcds:generatecache"; Check: returnFalse()
Filename: "{app}\Gargoyle.exe"; Description: "{cm:LaunchProgram,Gargoyle}"; Flags: nowait postinstall skipifsilent; Check: returnTrue()
Filename: "{app}\Gargoyle.exe"; Parameters: "-install -svcName ""Gargoyle"" -svcDesc ""Gargoyle"" -mainExe ""Gargoyle.exe""  "; Check: returnFalse()

[UninstallRun]
Filename: "{app}\Gargoyle.exe "; Parameters: "-uninstall -svcName Gargoyle -stopOnUninstall"; Check: returnFalse()

[Code]
function returnTrue(): Boolean;
begin
  Result := True;
end;

function returnFalse(): Boolean;
begin
  Result := False;
end;

function InitializeSetup(): Boolean;
begin
// Possible future improvements:
//   if version less or same => just launch app
//   if upgrade => check if same app is running and wait for it to exit
//   Add pack200/unpack200 support?
  Result := True;
end;
