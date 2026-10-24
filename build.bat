@echo off
echo ============================================================
echo Compiling Hospital Management System...
echo ============================================================
if not exist "target\classes" mkdir target\classes

dir /s /b src\main\java\*.java > sources.txt
javac -encoding UTF-8 -cp "lib\mysql-connector-j-8.3.0.jar" -d "target\classes" @sources.txt
del sources.txt

if %ERRORLEVEL% EQU 0 (
    echo.
    echo Build Successful! Run run.bat to launch the application.
) else (
    echo.
    echo Build Failed! Please check Java installation.
)
pause
