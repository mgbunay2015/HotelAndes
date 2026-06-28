@echo off
chcp 65001 >nul
cd /d "%~dp0"

echo Deteniendo Hotel Los Andes...
docker compose down

if %ERRORLEVEL% EQU 0 (
    echo Contenedores detenidos correctamente.
) else (
    echo Error al detener los contenedores.
)

pause
