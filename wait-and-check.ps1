Start-Sleep -Seconds 30

Write-Host "=== Gateway Log (last 50 lines) ===" -ForegroundColor Cyan
Get-Content d:\IDEA\project\cloud-govern\gateway.log -Tail 50 -ErrorAction SilentlyContinue

Write-Host ""
Write-Host "=== Auth Log (last 50 lines) ===" -ForegroundColor Cyan
Get-Content d:\IDEA\project\cloud-govern\auth.log -Tail 50 -ErrorAction SilentlyContinue

Write-Host ""
Write-Host "=== Service Health Check ===" -ForegroundColor Cyan

Write-Host "Gateway: " -NoNewline
try {
    $r = Invoke-WebRequest -Uri 'http://localhost:8080/actuator/health' -UseBasicParsing -TimeoutSec 5
    Write-Host "OK" -ForegroundColor Green
} catch {
    Write-Host "FAILED" -ForegroundColor Red
}

Write-Host "Auth: " -NoNewline
try {
    $r = Invoke-WebRequest -Uri 'http://localhost:8100/actuator/health' -UseBasicParsing -TimeoutSec 5
    Write-Host "OK" -ForegroundColor Green
} catch {
    Write-Host "FAILED" -ForegroundColor Red
}
