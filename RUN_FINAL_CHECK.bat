@echo off
setlocal
cd /d "%~dp0"

rem Use the Android Studio bundled JDK 21 rather than globally installed Java 26.
set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"
set "PATH=%JAVA_HOME%\bin;%PATH%"

if not exist "%JAVA_HOME%\bin\java.exe" (
  echo ERROR: Could not find Android Studio JBR at %JAVA_HOME%.
  echo Set JAVA_HOME to a compatible JDK 21 and rerun.
  exit /b 1
)

if not exist "local.properties" (
  echo WARNING: local.properties is missing.
  echo Build may pass with placeholder config, but login will not work.
)

echo.
echo [AMBRYN] Running JUnit tests and building the debug APK...
call "%~dp0gradlew.bat" clean testDebugUnitTest assembleDebug
if errorlevel 1 (
  echo.
  echo AMBRYN final check FAILED. Review the first Gradle error above.
  exit /b 1
)

echo.
echo AMBRYN final check PASSED: Gradle testDebugUnitTest + assembleDebug.
exit /b 0
