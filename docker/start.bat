@echo off
chcp 65001 >nul
echo ========================================
echo   Cloud Govern 基础设施启动脚本
echo ========================================
echo.

REM 检查 Docker 是否安装
docker --version >nul 2>&1
if errorlevel 1 (
    echo [错误] Docker 未安装或未添加到 PATH
    echo 请先安装 Docker Desktop: https://www.docker.com/products/docker-desktop
    pause
    exit /b 1
)

echo [信息] 启动基础设施服务...
echo.

cd /d "%~dp0"

REM 启动服务
docker compose up -d

echo.
echo ========================================
echo   服务启动完成
echo ========================================
echo.
echo   MySQL:    localhost:3306  (root/root123)
echo   Redis:    localhost:6379
echo   Nacos:    http://localhost:8848/nacos (nacos/nacos)
echo.
echo   请等待 Nacos 启动完成（约 1-2 分钟）
echo ========================================
pause
