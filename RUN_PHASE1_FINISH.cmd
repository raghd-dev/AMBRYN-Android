@echo off
cd /d "D:\AMBRYN_Release\AMBRYN"
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "tools\phase1\Finish.ps1" -ProjectRoot "D:\AMBRYN_Release\AMBRYN"
echo.
echo Phase 1 command finished. Read any errors above before continuing.
pause
