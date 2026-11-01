# run-all-services.ps1
$services = @(
    "auth-service",
    "user-service",
    "booking-service",
    "tracking-service",
    "payment-service",
    "notification-service",
    "review-service",
    "api-gateway"
)

Write-Host "Starting all microservices..." -ForegroundColor Cyan

foreach ($service in $services) {
    Write-Host "Starting $service..." -ForegroundColor Green
    # Using double quotes for the command and sub-expression $() for the service name
    $command = "cd backend-microservices; .\gradlew.bat :$($service):bootRun"
    Start-Process powershell -ArgumentList "-NoExit", "-Command", "$command"
    Start-Sleep -Seconds 5 
}

Write-Host "All services are starting in separate windows." -ForegroundColor Yellow
Write-Host "Note: Ensure your Databases (Postgres/Mongo) and Kafka are running!" -ForegroundColor Red
