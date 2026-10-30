@echo off
rem Stops the app, Gradle daemon, Kotlin daemon, and adb server.
rem Usage: scripts\stop-dev.bat          (pauses so the window stays open)
rem        scripts\stop-dev.bat -nopause (for terminals and scripts)

powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0stop-dev.ps1"

if /i not "%~1"=="-nopause" pause
