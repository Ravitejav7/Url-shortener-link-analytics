@echo off
setlocal

set "PROJECT_DIR=%~dp0"
set "JAVA_HOME=C:\Program Files\Java\jdk-21.0.11"
set "MAVEN_HOME=%PROJECT_DIR%.tools\apache-maven-3.9.16"

if not exist "%JAVA_HOME%\bin\java.exe" (
  echo JAVA_HOME is not valid: %JAVA_HOME%
  exit /b 1
)

if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
  echo Maven was not found at: %MAVEN_HOME%
  exit /b 1
)

set "PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%"
call "%MAVEN_HOME%\bin\mvn.cmd" %*
