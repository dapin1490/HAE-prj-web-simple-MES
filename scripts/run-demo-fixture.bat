@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
for %%I in ("%SCRIPT_DIR%..") do set "PROJECT_ROOT=%%~fI"

set "DIST_DIR=%PROJECT_ROOT%\demo-package\frontend-dist"
if not exist "%DIST_DIR%" set "DIST_DIR=%PROJECT_ROOT%\frontend\dist"

if not exist "%DIST_DIR%" (
  echo Frontend dist folder not found.
  echo Expected: "%PROJECT_ROOT%\demo-package\frontend-dist" or "%PROJECT_ROOT%\frontend\dist"
  exit /b 1
)

echo Starting fixture demo server at http://localhost:4173

where python >nul 2>&1
if %errorlevel%==0 (
  start "MES Fixture Demo" cmd /k "cd /d ""%DIST_DIR%"" && python -m http.server 4173"
  goto :eof
)

where npx >nul 2>&1
if %errorlevel%==0 (
  start "MES Fixture Demo" cmd /k "cd /d ""%DIST_DIR%"" && npx --yes serve . -l 4173"
  goto :eof
)

echo Neither python nor npx is available.
echo Install Python or Node.js on this PC.
exit /b 1
