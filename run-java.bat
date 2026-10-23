@echo off
setlocal
cd /d "%~dp0"

where javac >nul 2>nul
if errorlevel 1 (
    echo Java JDK was not found. Install a JDK and make sure javac is on PATH.
    pause
    exit /b 1
)

echo Compiling Spellseek.java...
javac Spellseek.java
if errorlevel 1 (
    echo Compilation failed. Please check the error messages above.
    pause
    exit /b 1
)

echo.
echo Starting Spellseek...
echo.
java Spellseek
pause
