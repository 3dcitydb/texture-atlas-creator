@ECHO OFF
cd /d %~dp0
if not "%JAVA_HOME%" == "" goto USE_JAVA_HOME
goto NO_JAVA_HOME

:NO_JAVA_HOME
set JAVA=java.exe
goto LAUNCH

:USE_JAVA_HOME
set JAVA="%JAVA_HOME%\bin\java.exe"
if not exist %JAVA% goto NO_JAVA_HOME
goto LAUNCH

:LAUNCH
%JAVA% -jar  -Xms1024m -Xmx1024m -XX:PermSize=64m -XX:MaxPermSize=64m TAC.jar %1 %2 %3 %4 %5 %6 %7 %8



