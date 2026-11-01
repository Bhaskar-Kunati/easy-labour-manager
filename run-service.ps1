param (
    [Parameter(Mandatory=$true)]
    [ValidateSet("auth-service", "user-service", "booking-service", "tracking-service", "payment-service", "notification-service", "review-service", "api-gateway")]
    [string]$ServiceName
)

Write-Host "Starting $ServiceName with 256MB memory limit..." -ForegroundColor Green

# Kill any existing instance of this specific service to prevent port conflicts
$portMap = @{
    "api-gateway" = 8080
    "auth-service" = 8081
    "user-service" = 8082
    "booking-service" = 8083
    "tracking-service" = 8084
    "payment-service" = 8085
    "notification-service" = 8086
    "review-service" = 8087
}

$port = $portMap[$ServiceName]
$proc = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue | Select-Object -ExpandProperty OwningProcess -First 1
if ($proc) {
    Write-Host "Stopping existing process on port $port..." -ForegroundColor Yellow
    Stop-Process -Id $proc -Force -ErrorAction SilentlyContinue
}

# Run with reduced memory footprint
$command = "cd backend-microservices; .\gradlew.bat :$($ServiceName):bootRun --args='--server.port=$port' -PjvmArgs='-Xmx256m -Xms128m'"

# Note: We use environmental variable for Gradle JVM args if the project supports it, 
# but the most reliable way for bootRun is setting it in the command or gradlew.bat
$command = "cd backend-microservices; `$env:JAVA_OPTS='-Xmx256m -Xms128m'; .\gradlew.bat :$($ServiceName):bootRun"

Start-Process powershell -ArgumentList "-NoExit", "-Command", "$command"

Write-Host "$ServiceName is starting in a new window." -ForegroundColor Yellow
