@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
for %%I in ("%SCRIPT_DIR%..") do set "PROJECT_ROOT=%%~fI"
set "PACKAGE_DIR=%PROJECT_ROOT%\demo-package"
set "FRONTEND_DIR=%PROJECT_ROOT%\frontend"
set "BACKEND_DIR=%PROJECT_ROOT%\be-mes-project"
set "CSV_SOURCE_DIR=%PROJECT_ROOT%\data\backend_ready"
set "BACKEND_RESOURCE_DATA_DIR=%BACKEND_DIR%\src\main\resources\data"

echo [1/5] Preparing package directory...
if exist "%PACKAGE_DIR%" rmdir /s /q "%PACKAGE_DIR%"
mkdir "%PACKAGE_DIR%\frontend-dist" || goto :error
mkdir "%PACKAGE_DIR%\backend" || goto :error

echo [2/5] Copying CSV files to backend resources (overwrite enabled)...
if not exist "%CSV_SOURCE_DIR%" (
  echo CSV source folder not found: "%CSV_SOURCE_DIR%"
  exit /b 1
)
if not exist "%CSV_SOURCE_DIR%\Products.csv" (
  echo Missing required CSV: "%CSV_SOURCE_DIR%\Products.csv"
  exit /b 1
)
if not exist "%CSV_SOURCE_DIR%\SalesOrders.csv" (
  echo Missing required CSV: "%CSV_SOURCE_DIR%\SalesOrders.csv"
  exit /b 1
)
if not exist "%CSV_SOURCE_DIR%\WorkOrders.csv" (
  echo Missing required CSV: "%CSV_SOURCE_DIR%\WorkOrders.csv"
  exit /b 1
)
if not exist "%CSV_SOURCE_DIR%\ProductionLogs.csv" (
  echo Missing required CSV: "%CSV_SOURCE_DIR%\ProductionLogs.csv"
  exit /b 1
)
if not exist "%CSV_SOURCE_DIR%\Inspections.csv" (
  echo Missing required CSV: "%CSV_SOURCE_DIR%\Inspections.csv"
  exit /b 1
)
if not exist "%BACKEND_RESOURCE_DATA_DIR%" mkdir "%BACKEND_RESOURCE_DATA_DIR%" || goto :error
xcopy "%CSV_SOURCE_DIR%\*.csv" "%BACKEND_RESOURCE_DATA_DIR%\" /i /y >nul || goto :error

echo [3/5] Building frontend...
pushd "%FRONTEND_DIR%" || goto :error
call npm ci || goto :error
call npm run build || goto :error
xcopy "%FRONTEND_DIR%\dist\*" "%PACKAGE_DIR%\frontend-dist\" /e /i /y >nul || goto :error
popd

echo [4/5] Building backend...
pushd "%BACKEND_DIR%" || goto :error
call gradlew.bat bootJar || goto :error
for %%F in ("%BACKEND_DIR%\build\libs\*.jar") do (
  copy "%%~fF" "%PACKAGE_DIR%\backend\" >nul || goto :error
)
popd

echo [5/5] Copying run scripts...
copy "%PROJECT_ROOT%\scripts\run-demo-fixture.bat" "%PACKAGE_DIR%\" >nul || goto :error
copy "%PROJECT_ROOT%\scripts\run-demo-fullstack.bat" "%PACKAGE_DIR%\" >nul || goto :error

echo.
echo Package created: "%PACKAGE_DIR%"
echo Move this folder to the presentation PC.
goto :eof

:error
echo.
echo Build/package failed. Check logs above.
exit /b 1
