@REM ----------------------------------------------------------------------------
@REM Maven Wrapper startup script for Windows, sourced from the Apache Maven
@REM Wrapper project (https://maven.apache.org/wrapper/) licensed under ASL 2.0.
@REM ----------------------------------------------------------------------------

@echo off
@setlocal

set ERROR_CODE=0

@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" @setlocal

set MAVEN_PROJECTBASEDIR=%~dp0
if "%MAVEN_PROJECTBASEDIR:~-1%"=="\" set MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR:~0,-1%

set WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
set WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

if not "%JAVA_HOME%"=="" goto OkJHome

@REM JAVA_HOME not set: fall back to java.exe found on PATH
for /f "delims=" %%J in ('where java 2^>nul') do (
    set JAVACMD=%%J
    goto init
)

echo.
echo Error: JAVA_HOME not found in your environment and no java.exe on PATH. >&2
echo Please set the JAVA_HOME variable in your environment to match the >&2
echo location of your Java installation. >&2
echo.
goto error

:OkJHome
if exist "%JAVA_HOME%\bin\java.exe" goto UseJavaHome

echo.
echo Error: JAVA_HOME is set to an invalid directory: %JAVA_HOME% >&2
echo Please set the JAVA_HOME variable in your environment to match the >&2
echo location of your Java installation. >&2
echo.
goto error

:UseJavaHome
set JAVACMD=%JAVA_HOME%\bin\java.exe

:init

set MAVEN_CMD_LINE_ARGS=%*

"%JAVACMD%" %MAVEN_OPTS% %MAVEN_DEBUG_OPTS% -classpath %WRAPPER_JAR% "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" %WRAPPER_LAUNCHER% %MAVEN_CMD_LINE_ARGS%
if ERRORLEVEL 1 goto error
goto end

:error
set ERROR_CODE=1

:end
@endlocal & set ERROR_CODE=%ERROR_CODE%

exit /B %ERROR_CODE%
