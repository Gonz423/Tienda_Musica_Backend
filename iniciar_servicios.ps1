Write-Host "========================================================" -ForegroundColor Cyan
Write-Host " Iniciando los 5 Microservicios y Frontend - Sonido Vivo" -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan
Write-Host ""

$backendDir = $PSScriptRoot
$frontendDir = [System.IO.Path]::GetFullPath((Join-Path $PSScriptRoot "..\Tienda_Musica_Frontend"))

Write-Host "[1/6] Levantando auth-service (Puerto 8081 - tienda_musica_auth)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$backendDir\auth-service'; ./mvnw spring-boot:run"

Write-Host "[2/6] Levantando usuario-service (Puerto 8082 - tienda_musica_usuarios)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$backendDir\usuario-service'; ./mvnw spring-boot:run"

Write-Host "[3/6] Levantando producto-service (Puerto 8083 - tienda_musica_productos)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$backendDir\producto-service'; ./mvnw spring-boot:run"

Write-Host "[4/6] Levantando pedido-service (Puerto 8084 - tienda_musica_pedidos)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$backendDir\pedido-service'; ./mvnw spring-boot:run"

Write-Host "[5/6] Levantando metrica-service (Puerto 8085 - tienda_musica_metricas)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$backendDir\metrica-service'; ./mvnw spring-boot:run"

if (Test-Path $frontendDir) {
    Write-Host "[6/6] Levantando Frontend React (Puerto 5173)..." -ForegroundColor Yellow
    Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$frontendDir'; npm run dev"
}

Write-Host ""
Write-Host "========================================================" -ForegroundColor Green
Write-Host "   Los 5 microservicios y el Frontend fueron iniciados!  " -ForegroundColor Green
Write-Host "========================================================" -ForegroundColor Green
