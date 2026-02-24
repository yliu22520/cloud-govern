@echo off
echo ========================================
echo  Cloud Govern - Check Services
echo ========================================
echo.

echo [Gateway - 8080]
curl -s http://localhost:8080/actuator/health
echo.
echo.

echo [Auth - 8100]
curl -s http://localhost:8100/actuator/health
echo.
echo.

echo [System - 8200]
curl -s http://localhost:8200/actuator/health
echo.
echo.

echo [Frontend - 3000]
curl -s -o nul -w "HTTP Status: %%{http_code}" http://localhost:3000
echo.
echo.

echo ========================================
pause
