@echo off
cd /d "D:\AMBRYN_Release\AMBRYN"
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "tools\phase1\Preflight.ps1" -ProjectRoot "D:\AMBRYN_Release\AMBRYN"
echo.
pause
