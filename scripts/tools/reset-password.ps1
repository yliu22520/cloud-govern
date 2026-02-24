# Reset password with detailed error
Write-Host "Resetting password..." -ForegroundColor Cyan
try {
    $response = Invoke-WebRequest -Uri 'http://localhost:8080/auth/reset-password?username=admin&password=admin123' -Method GET -UseBasicParsing
    Write-Host "Response:" -ForegroundColor Green
    Write-Host $response.Content
} catch {
    Write-Host "Error:" -ForegroundColor Red
    Write-Host $_.Exception.Message
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $reader.BaseStream.Position = 0
        $responseBody = $reader.ReadToEnd()
        Write-Host "Response Body:" -ForegroundColor Yellow
        Write-Host $responseBody
    }
}
