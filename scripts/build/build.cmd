@echo off
echo ========================================
echo  Cloud Govern - Build Project
echo ========================================
echo.

set "JAVA_HOME=D:\devlop\jdk-21.0.2"
set "PATH=%JAVA_HOME%\bin;%PATH%"
set "MAVEN_CMD=D:\devlop\idea25\IntelliJ IDEA 2025.3.1\plugins\maven\lib\maven3\bin\mvn.cmd"

cd /d d:\IDEA\project\cloud-govern

echo Building project...
"%MAVEN_CMD%" clean install -DskipTests

echo.
echo ========================================
if %ERRORLEVEL% EQU 0 (
    echo  Build SUCCESS!
) else (
    echo  Build FAILED!
)
echo ========================================
pause
