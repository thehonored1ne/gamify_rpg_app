# Stops every background process this project starts.
# Run through scripts\stop-dev.bat, or directly:
#   powershell -NoProfile -ExecutionPolicy Bypass -File scripts\stop-dev.ps1

$ErrorActionPreference = 'SilentlyContinue'

function Get-DevProcesses {
  $found = @()
  $found += Get-CimInstance Win32_Process -Filter "Name='java.exe'" |
    Where-Object {
      $_.CommandLine -like '*GradleDaemon*' -or $_.CommandLine -like '*KotlinCompileDaemon*'
    } |
    ForEach-Object { [pscustomobject]@{ Id = $_.ProcessId; Name = 'java daemon' } }
  $found += Get-Process adb -ErrorAction SilentlyContinue |
    ForEach-Object { [pscustomobject]@{ Id = $_.Id; Name = 'adb' } }
  return $found
}

$root = Split-Path -Parent $PSScriptRoot
Push-Location $root

Write-Host 'Stopping the app on the device...'
adb shell am force-stop com.example.liferpg 2>$null

Write-Host 'Stopping the Gradle daemon...'
& .\gradlew.bat --stop

Write-Host 'Stopping the Kotlin compile daemon...'
Get-CimInstance Win32_Process -Filter "Name='java.exe'" |
  Where-Object { $_.CommandLine -like '*KotlinCompileDaemon*' } |
  ForEach-Object {
    Write-Host "  stopping PID $($_.ProcessId)"
    Stop-Process -Id $_.ProcessId -Force
  }

Write-Host 'Stopping the adb server...'
adb kill-server

Pop-Location

# Gradle shuts its daemon down asynchronously, so give it time before judging.
Write-Host ''
Write-Host 'Waiting for processes to exit...'
$deadline = (Get-Date).AddSeconds(20)
do {
  Start-Sleep -Milliseconds 500
  $left = @(Get-DevProcesses)
} while ($left.Count -gt 0 -and (Get-Date) -lt $deadline)

if ($left.Count -gt 0) {
  Write-Host 'Force-stopping leftovers...'
  $left | ForEach-Object {
    Write-Host "  killing $($_.Name) PID $($_.Id)"
    Stop-Process -Id $_.Id -Force
  }
  Start-Sleep -Seconds 2
  $left = @(Get-DevProcesses)
}

if ($left.Count -gt 0) {
  Write-Host 'Still running (not started by this project?):'
  $left | Select-Object Id, Name | Format-Table -AutoSize
  exit 1
}

Write-Host 'All development processes stopped.'
