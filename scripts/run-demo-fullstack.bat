@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
for %%I in ("%SCRIPT_DIR%..") do set "PROJECT_ROOT=%%~fI"

set "DIST_DIR=%PROJECT_ROOT%\demo-package\frontend-dist"
if not exist "%DIST_DIR%" set "DIST_DIR=%PROJECT_ROOT%\frontend\dist"

set "JAR_FILE="
for %%F in ("%PROJECT_ROOT%\demo-package\backend\*.jar") do (
  set "JAR_FILE=%%~fF"
  goto :jar_found
)
for %%F in ("%PROJECT_ROOT%\be-mes-project\build\libs\*.jar") do (
  set "JAR_FILE=%%~fF"
  goto :jar_found
)

:jar_found
if "%JAR_FILE%"=="" (
  echo Backend jar file not found.
  echo Run scripts\build-presentation-package.bat first.
  exit /b 1
)

if not exist "%DIST_DIR%" (
  echo Frontend dist folder not found.
  echo Run scripts\build-presentation-package.bat first.
  exit /b 1
)

where java >nul 2>&1
if not %errorlevel%==0 (
  echo Java is not available. Install Java 17 or newer.
  exit /b 1
)

echo Starting backend: "%JAR_FILE%"
start "MES Backend" cmd /k "java -jar ""%JAR_FILE%"""

echo Starting frontend server at http://localhost:4173
where python >nul 2>&1
if %errorlevel%==0 (
  start "MES Frontend" cmd /k "cd /d ""%DIST_DIR%"" && python -m http.server 4173"
  goto :eof
)

where npx >nul 2>&1
if %errorlevel%==0 (
  start "MES Frontend" cmd /k "cd /d ""%DIST_DIR%"" && npx --yes serve . -l 4173"
  goto :eof
)

echo Neither python nor npx is available.
echo Install Python or Node.js on this PC.
exit /b 1
