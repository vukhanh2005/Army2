@echo off
setlocal
cd /d "%~dp0"

set "APP_DIR=%~dp0"
set "JAR_FILE=%APP_DIR%MobiArmy.jar"
set "JAVA_EXE=%~dp0runtime\bin\java.exe"
if not exist "%JAVA_EXE%" (
  set "JAVA_EXE=java"
)

if not exist "%JAR_FILE%" (
  echo Unable to find "%JAR_FILE%".
  echo.
  echo Current directory:
  cd
  echo.
  echo Files beside this launcher:
  dir "%APP_DIR%"
  pause
  exit /b 1
)

"%JAVA_EXE%" -version >nul 2>nul
if errorlevel 1 (
  echo Java was not found. Reinstall the server package or install JDK/JRE 21 or newer.
  pause
  exit /b 1
)

"%JAVA_EXE%" -jar "%JAR_FILE%"
pause
