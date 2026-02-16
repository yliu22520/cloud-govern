Write-Host "Checking Cloud Govern Services..." -ForegroundColor Cyan
Write-Host ""

# Check Gateway
Write-Host "Gateway (8080):" -NoNewline
try {
    $response = Invoke-WebRequest -Uri 'http://localhost:8080/actuator/health' -UseBasicParsing -TimeoutSec 5
    Write-Host " OK" -ForegroundColor Green
    Write-Host $response.Content
} catch {
    Write-Host " NOT RUNNING" -ForegroundColor Red
    Write-Host $_.Exception.Message
}

Write-Host ""

# Check Auth
Write-Host "Auth (8100):" -NoNewline
try {
    $response = Invoke-WebRequest -Uri 'http://localhost:8100/actuator/health' -UseBasicParsing -TimeoutSec 5
    Write-Host " OK" -ForegroundColor Green
    Write-Host $response.Content
} catch {
    Write-Host " NOT RUNNING" -ForegroundColor Red
    Write-Host $_.Exception.Message
}

Write-Host ""

# Check Nacos services
Write-Host "Nacos Registered Services:" -ForegroundColor Cyan
try {
    $response = Invoke-WebRequest -Uri 'http://localhost:8848/nacos/v1/ns/service/list?pageNo=1&pageSize=10' -UseBasicParsing -TimeoutSec 5
    Write-Host $response.Content
} catch {
    Write-Host "Nacos not accessible"
}
