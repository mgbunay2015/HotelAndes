@echo off
chcp 65001 >nul
cd /d "%~dp0"

docker info >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Docker no esta en ejecucion.
    pause
    exit /b 1
)

echo ========================================
echo  Limpieza completa - Hotel Los Andes
echo ========================================
echo.
echo Esto eliminara:
echo   - Contenedores del proyecto
echo   - Volumenes rooms_data y reservations_data (BD vacias)
echo   - Imagenes locales hotel-svc-rooms y hotel-svc-reservations
echo.
set /p confirmar="Continuar? (S/N): "
if /i not "%confirmar%"=="S" (
    echo Operacion cancelada.
    pause
    exit /b 0
)

echo.
echo [1/2] Eliminando contenedores, volumenes e imagenes...
docker compose down -v --rmi local --remove-orphans
if %ERRORLEVEL% NEQ 0 (
    echo Error al limpiar Docker.
    pause
    exit /b 1
)

echo.
echo [2/2] Reconstruyendo y levantando contenedores...
docker compose up -d --build
if %ERRORLEVEL% NEQ 0 (
    echo Error al reconstruir los contenedores.
    pause
    exit /b 1
)

echo.
echo ========================================
echo  Listo. Stack reconstruido desde cero.
echo ========================================
echo.
echo Espera ~30 segundos y prueba:
echo   http://localhost/api/v1/rooms
echo.
pause
