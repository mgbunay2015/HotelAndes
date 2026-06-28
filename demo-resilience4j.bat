@echo off
chcp 65001 >nul
cd /d "%~dp0"

echo ========================================
echo  Demo Resilience4j - Hotel Los Andes
echo  (Pruebas 8, 9 y 10 de la actividad)
echo ========================================
echo.

docker info >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Docker no esta en ejecucion.
    pause
    exit /b 1
)

docker ps --filter "name=svc-reservations" --filter "status=running" -q >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] svc-reservations no esta corriendo.
    echo Ejecuta primero: ejecutar.bat
    pause
    exit /b 1
)

echo PASO 1: Detener svc-rooms para simular fallo...
docker stop svc-rooms
echo.
echo svc-rooms DETENIDO.
echo.
echo PASO 2: Prueba en Postman o PowerShell:
echo.
echo   [Prueba 8] Crear reserva - debe retornar 503:
echo   POST http://localhost/api/v1/reservations
echo   Body: {"roomId":1,"guestName":"Test","guestEmail":"test@test.com",
echo          "checkInDate":"2026-07-01","checkOutDate":"2026-07-03"}
echo.
echo   [Prueba 9] Consultar reserva existente - debe retornar 200 con fallback:
echo   GET http://localhost/api/v1/reservations/1
echo   roomNumber = "Room information temporarily unavailable"
echo.
pause

echo.
echo PASO 3: Reiniciar svc-rooms para recuperacion...
docker start svc-rooms
echo.
echo Esperando recuperacion (15 segundos)...
timeout /t 15 /nobreak >nul
echo.
echo [Prueba 10] El sistema debe funcionar normalmente.
echo Prueba de nuevo GET /api/v1/reservations/1 en Postman.
echo.
echo Circuit Breaker deberia cerrarse automaticamente tras reiniciar svc-rooms.
echo.
pause
