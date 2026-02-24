# Test direct login and check Redis
Write-Host "=== Testing Auth Service Login ===" -ForegroundColor Green

$loginBody = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

# First, clear any existing login
$loginResult = Invoke-RestMethod -Uri 'http://localhost:8100/auth/login' -Method POST -ContentType 'application/json' -Body $loginBody
$loginResult | ConvertTo-Json -Depth 5

$token = $loginResult.data.accessToken
Write-Host "`nToken: $token" -ForegroundColor Yellow

# Check Redis for satoken keys
Write-Host "`n=== Checking Redis for Sa-Token Data ===" -ForegroundColor Green
docker exec cloud-govern-redis redis-cli KEYS "*"

# Try to get satoken token value
Write-Host "`n=== Checking Sa-Token Keys ===" -ForegroundColor Green
docker exec cloud-govern-redis redis-cli KEYS "*satoken*"
docker exec cloud-govern-redis redis-cli KEYS "*token*"
