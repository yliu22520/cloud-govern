@echo off
echo Checking services...
echo.
echo Gateway (8080):
curl -s http://localhost:8080/actuator/health
echo.
echo.
echo Auth (8100):
curl -s http://localhost:8100/actuator/health
echo.
echo.
echo Nacos Services:
curl -s "http://localhost:8848/nacos/v1/ns/service/list?pageNo=1&pageSize=10"
echo.
pause
