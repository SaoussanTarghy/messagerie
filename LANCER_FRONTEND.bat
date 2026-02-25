@echo off
title Lancement Frontend React - Planification Academique
color 0A

echo.
echo ==========================================
echo   DEMARRAGE DU FRONTEND REACT
echo   Planification Academique - Calendrier
echo ==========================================
echo.

:: Aller dans le dossier frontend
cd /d "%~dp0frontend"

:: Verifier si Node.js est installe
echo [1/3] Verification de Node.js...
where node >nul 2>&1
if %errorlevel% neq 0 (
    echo ERREUR: Node.js n'est pas installe!
    echo Telecharger depuis: https://nodejs.org/
    pause
    exit /b 1
)
node --version
echo OK: Node.js installe

echo.
:: Installer les dependances npm si node_modules n'existe pas
echo [2/3] Installation des dependances npm...
if not exist "node_modules\" (
    echo Installation en cours, patientez...
    npm install
    if %errorlevel% neq 0 (
        echo ERREUR lors de npm install!
        pause
        exit /b 1
    )
) else (
    echo OK: node_modules deja present
)

echo.
echo [3/3] Demarrage du serveur React...
echo.
echo ==========================================
echo   FRONTEND LANCE : http://localhost:5173/
echo   (Ouverture automatique du navigateur)
echo ==========================================
echo.
echo Pour arreter: appuyer sur Ctrl+C
echo.

:: Attendre 3 secondes puis ouvrir le navigateur
start /b cmd /c "timeout /t 3 /nobreak >nul && start http://localhost:5173/"

:: Lancer Vite
npm run dev
