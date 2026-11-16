@echo off
title Lanzador de Servicios - Tienda de Musica Sonido Vivo
echo ========================================================
echo   Iniciando los 5 Microservicios y Frontend - Sonido Vivo
echo ========================================================
echo.

echo [1/6] Levantando auth-service (Puerto 8081 - tienda_musica_auth)...
start "Auth Service - 8081" cmd /k "cd /d "%~dp0auth-service" && mvnw.cmd spring-boot:run"

echo [2/6] Levantando usuario-service (Puerto 8082 - tienda_musica_usuarios)...
start "Usuario Service - 8082" cmd /k "cd /d "%~dp0usuario-service" && mvnw.cmd spring-boot:run"

echo [3/6] Levantando producto-service (Puerto 8083 - tienda_musica_productos)...
start "Producto Service - 8083" cmd /k "cd /d "%~dp0producto-service" && mvnw.cmd spring-boot:run"

echo [4/6] Levantando pedido-service (Puerto 8084 - tienda_musica_pedidos)...
start "Pedido Service - 8084" cmd /k "cd /d "%~dp0pedido-service" && mvnw.cmd spring-boot:run"

echo [5/6] Levantando metrica-service (Puerto 8085 - tienda_musica_metricas)...
start "Metrica Service - 8085" cmd /k "cd /d "%~dp0metrica-service" && mvnw.cmd spring-boot:run"

echo [6/6] Levantando Frontend React (Puerto 5173)...
start "Frontend Vite - 5173" cmd /k "cd /d "%~dp0..\Tienda_Musica_Frontend" && npm run dev"

echo.
echo ========================================================
echo   Los 5 microservicios y el Frontend fueron iniciados!
echo ========================================================
pause
