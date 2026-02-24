# Test BCrypt password hash
Add-Type -AssemblyName System.Web

# The hash from database
$dbHash = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKTNDp8G3vQMWR.XHGJ.Z5QX.ZQq'

# Test if we can verify with the correct password
Write-Host "Testing BCrypt hash verification..."
Write-Host "Database hash: $dbHash"
Write-Host ""

# Query database to check actual password value
$mysqlQuery = "SELECT id, username, password FROM sys_user WHERE username='admin'"
Write-Host "Please manually verify the password in MySQL:"
Write-Host "  mysql -u root -p cloud_govern -e `"SELECT id, username, password FROM sys_user WHERE username='admin'`""
