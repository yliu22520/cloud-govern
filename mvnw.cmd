@echo off
setlocal

set MAVEN_HOME=D:\devlop\idea25\IntelliJ IDEA 2025.3.1\plugins\maven\lib\maven3
set PATH=%MAVEN_HOME%\bin;%PATH%

"%MAVEN_HOME%\bin\mvn.cmd" %*

endlocal
