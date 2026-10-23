@echo off
setlocal
cd /d "%~dp0\.."

where javac >nul 2>nul || (
    echo ERROR: Java JDK 8 or newer is required.
    exit /b 2
)

where java >nul 2>nul || (
    echo ERROR: Java JDK 8 or newer is required.
    exit /b 2
)

set "BUILD_DIR=%~dp0.build"

if not exist "%BUILD_DIR%" mkdir "%BUILD_DIR%"

javac -encoding UTF-8 -d "%BUILD_DIR%" Spellseek.java "%~dp0SpellseekTest.java"

if errorlevel 1 goto failed

java -cp "%BUILD_DIR%" SpellseekTest "%~dp0fixtures"

set "RESULT=%ERRORLEVEL%"
rmdir /s /q "%BUILD_DIR%" >nul 2>nul
exit /b %RESULT%

:failed
rmdir /s /q "%BUILD_DIR%" >nul 2>nul
exit /b 1
