@echo off
echo Starting Cloud Govern Services...

:: Start Gateway
start "Gateway" cmd /k "set JAVA_HOME=D:\devlop\jdk-21.0.2 && cd /d d:\IDEA\project\cloud-govern && D:\devlop\idea25\IntelliJ IDEA 2025.3.1\plugins\maven\lib\maven3\bin\mvn.cmd -pl cloud-govern-gateway spring-boot:run"

:: Wait for Gateway to start
timeout /t 10 /nobreak > nul

:: Start Auth
start "Auth" cmd /k "set JAVA_HOME=D:\devlop\jdk-21.0.2 && cd /d d:\IDEA\project\cloud-govern && D:\devlop\idea25\IntelliJ IDEA 2025.3.1\plugins\maven\lib\maven3\bin\mvn.cmd -pl cloud-govern-auth spring-boot:run"

echo Services starting... Please wait for both windows to show "Started" message.
echo Gateway: http://localhost:8080
echo Auth: http://localhost:8100
