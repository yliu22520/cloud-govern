@echo off
echo Starting Auth (port 8100)...

set "JAVA_HOME=D:\devlop\jdk-21.0.2"
set "PATH=%JAVA_HOME%\bin;%PATH%"
set "MAVEN_CMD=D:\devlop\idea25\IntelliJ IDEA 2025.3.1\plugins\maven\lib\maven3\bin\mvn.cmd"

cd /d d:\IDEA\project\cloud-govern\cloud-govern-auth
"%MAVEN_CMD%" spring-boot:run
