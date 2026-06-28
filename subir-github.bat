@echo off
chcp 65001 >nul
cd /d "%~dp0"

set REPO=https://github.com/mgbunay2015/HotelAndes.git

echo ========================================
echo  Subir proyecto a GitHub
echo  %REPO%
echo ========================================
echo.

where git >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Git no esta instalado o no esta en el PATH.
    pause
    exit /b 1
)

if not exist ".git" (
    echo Inicializando repositorio git...
    git init
    git branch -M main
)

git remote get-url origin >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo Configurando remote origin...
    git remote add origin %REPO%
) else (
    git remote set-url origin %REPO%
)

echo.
echo Archivos a subir:
git status -s
echo.

set /p confirmar="Agregar todos los archivos y hacer commit? (S/N): "
if /i not "%confirmar%"=="S" (
    echo Operacion cancelada.
    pause
    exit /b 0
)

git add .
git commit -m "Sistema de reservas Hotel Los Andes - microservicios svc-rooms y svc-reservations"
if %ERRORLEVEL% NEQ 0 (
    echo No hay cambios nuevos para commitear, o el commit fallo.
)

echo.
echo Subiendo a GitHub...
git push -u origin main
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [AVISO] Push fallo. Verifica que estas autenticado en GitHub.
    echo Puedes usar: gh auth login
    echo O configurar un Personal Access Token.
    pause
    exit /b 1
)

echo.
echo Listo. Repositorio: %REPO%
pause
