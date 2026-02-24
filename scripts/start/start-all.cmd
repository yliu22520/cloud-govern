@echo off
echo ========================================
echo  Cloud Govern - Start All Services
echo ========================================
echo.

set "JAVA_HOME=D:\devlop\jdk-21.0.2"
set "PATH=%JAVA_HOME%\bin;%PATH%"
set "MAVEN_CMD=D:\devlop\idea25\IntelliJ IDEA 2025.3.1\plugins\maven\lib\maven3\bin\mvn.cmd"
set "PROJECT_DIR=d:\IDEA\project\cloud-govern"

echo [1/3] Starting Gateway (port 8080)...
start "Gateway" cmd /k "set "JAVA_HOME=D:\devlop\jdk-21.0.2" && set "PATH=D:\devlop\jdk-21.0.2\bin;%PATH%" && cd /d %PROJECT_DIR% && "%MAVEN_CMD%" -pl cloud-govern-gateway spring-boot:run"

timeout /t 5 /nobreak > nul

echo [2/3] Starting Auth (port 8100)...
start "Auth" cmd /k "set "JAVA_HOME=D:\devlop\jdk-21.0.2" && set "PATH=D:\devlop\jdk-21.0.2\bin;%PATH%" && cd /d %PROJECT_DIR% && "%MAVEN_CMD%" -pl cloud-govern-auth spring-boot:run"

timeout /t 5 /nobreak > nul

echo [3/3] Starting System (port 8200)...
start "System" cmd /k "set "JAVA_HOME=D:\devlop\jdk-21.0.2" && set "PATH=D:\devlop\jdk-21.0.2\bin;%PATH%" && cd /d %PROJECT_DIR% && "%MAVEN_CMD%" -pl cloud-govern-system spring-boot:run"

echo.
echo ========================================
echo  All services are starting...
echo  Gateway:  http://localhost:8080
echo  Auth:     http://localhost:8100
echo  System:   http://localhost:8200
echo ========================================
pause
