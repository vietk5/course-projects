@echo off
REM Wrapper de chay nuclear-push.ps1 voi quyen bypass execution policy
cd /d "%~dp0"
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0nuclear-push.ps1"
