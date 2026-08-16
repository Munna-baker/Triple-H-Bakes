@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem  Gradle startup script for Windows
@rem ##########################################################################

@setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%

@rem Determine the Java command to use to start the JVM.
if not "%JAVA_HOME%" == "" goto have_java_home

set JAVACMD=java.exe
%JAVACMD% --version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto have_javacmd

echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
goto fail

:have_java_home
set JAVACMD=%JAVA_HOME%\bin\java.exe
if exist "%JAVACMD%" goto have_javacmd

echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
goto fail

:have_javacmd
"%JAVACMD%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% -jar "%APP_HOME%\gradle\wrapper\gradle-wrapper.jar" %*
goto end

:fail
exit /b 1

:end
endlocal
