@echo off
powershell -Command "Invoke-WebRequest -Uri 'https://download.java.net/java/GA/jdk21.0.2/f2283984656d49d69e91c558476027ac/13/GPL/openjdk-21.0.2_windows-x64_bin.zip' -OutFile 'D:\devlop\jdk21.zip'"
powershell -Command "Expand-Archive -Path 'D:\devlop\jdk21.zip' -DestinationPath 'D:\devlop' -Force"
