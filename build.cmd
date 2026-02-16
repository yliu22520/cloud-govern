@echo off
cd /d %~dp0
"D:\devlop\idea25\IntelliJ IDEA 2025.3.1\plugins\maven\lib\maven3\bin\mvn.cmd" clean install -DskipTests
pause
