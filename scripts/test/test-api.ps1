# Test Login API
$loginBody = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

Write-Host "=== Testing Login API ===" -ForegroundColor Green
$loginResult = Invoke-RestMethod -Uri 'http://localhost:8100/auth/login' -Method POST -ContentType 'application/json' -Body $loginBody
$loginResult | ConvertTo-Json -Depth 5

$token = $loginResult.data.accessToken
Write-Host "`nToken: $token" -ForegroundColor Yellow

# Set Authorization header
$headers = @{
    Authorization = "Bearer $token"
}

# Test User List API (Through Gateway)
Write-Host "`n=== Testing User List API (Through Gateway) ===" -ForegroundColor Green
try {
    $userList = Invoke-RestMethod -Uri 'http://localhost:8080/system/user/list?pageNum=1&pageSize=10' -Method GET -Headers $headers
    $userList | ConvertTo-Json -Depth 5
} catch {
    Write-Host "Error: $_" -ForegroundColor Red
}

# Test Role List API (Through Gateway)
Write-Host "`n=== Testing Role List API (Through Gateway) ===" -ForegroundColor Green
try {
    $roleList = Invoke-RestMethod -Uri 'http://localhost:8080/system/role/list?pageNum=1&pageSize=10' -Method GET -Headers $headers
    $roleList | ConvertTo-Json -Depth 5
} catch {
    Write-Host "Error: $_" -ForegroundColor Red
}

# Test Menu List API (Through Gateway)
Write-Host "`n=== Testing Menu List API (Through Gateway) ===" -ForegroundColor Green
try {
    $menuList = Invoke-RestMethod -Uri 'http://localhost:8080/system/menu/list' -Method GET -Headers $headers
    $menuList | ConvertTo-Json -Depth 5
} catch {
    Write-Host "Error: $_" -ForegroundColor Red
}

Write-Host "`n=== All API Tests Completed ===" -ForegroundColor Green
