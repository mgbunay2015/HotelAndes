@echo off
chcp 65001 >nul
cd /d "%~dp0"

echo ========================================
echo  Hotel Los Andes - Microservicios
echo  GitHub: https://github.com/mgbunay2015/HotelAndes
echo ========================================
echo.

docker info >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Docker no esta en ejecucion.
    echo Abre Docker Desktop, espera a que arranque y vuelve a ejecutar este archivo.
    pause
    exit /b 1
)

netstat -ano | findstr /R /C:":80 .*LISTENING" >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo [AVISO] El puerto 80 ya esta en uso.
    echo Si otro servicio lo ocupa, detenlo o cambia el puerto en docker-compose.yml
    echo.
)

echo Construyendo y levantando contenedores...
docker compose up -d --build
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] No se pudieron crear los contenedores.
    pause
    exit /b 1
)

echo.
echo Esperando que Spring Boot arranque (30 segundos)...
timeout /t 30 /nobreak >nul

echo.
echo ========================================
echo  Sistema listo - API Gateway :80
echo ========================================
echo.
echo  svc-rooms:
echo    GET  http://localhost/api/v1/rooms
echo    POST http://localhost/api/v1/rooms
echo    GET  http://localhost/api/v1/rooms/{id}/availability
echo.
echo  svc-reservations:
echo    POST  http://localhost/api/v1/reservations
echo    GET   http://localhost/api/v1/reservations/{id}
echo    GET   http://localhost/api/v1/reservations/guest/{email}
echo    PATCH http://localhost/api/v1/reservations/{id}/checkout
echo.
echo  Puertos directos (sin nginx):
echo    svc-rooms:        http://localhost:8081
echo    svc-reservations: http://localhost:8082
echo    rooms-db:         localhost:5433
echo    reservations-db:  localhost:5434
echo.
echo  Postman: importar postman\Hotel_Los_Andes.postman_collection.json
echo  Manual:  Manual_Hotel_Microservicios.pdf
echo.
echo  Comandos utiles:
echo    Ver logs:     docker compose logs -f
echo    Ver estado:   docker compose ps
echo    Detener:      detener.bat
echo    Limpiar todo: limpiar-docker.bat
echo    Demo Resilience4j: demo-resilience4j.bat
echo.
pause
