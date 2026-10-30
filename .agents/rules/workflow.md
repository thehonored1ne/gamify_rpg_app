---
description: Local dev workflow - gradle cli, zed, adb device testing
globs: ["**/*"]
alwaysApply: true
---

- no android studio; build with the gradle wrapper only.
- commands: `.\gradlew assembleDebug`, `.\gradlew test`, `.\gradlew installDebug`.
- device serial: 15187705C5010595.
- use `adb logcat`, `adb shell screencap`, and `adb shell run-as` for verification.
- shell is powershell: no `&&`, heredocs, `head`, `tail`, `sed`.
- compileSdk stays 36; agp 9.0.1 has no android-37 target.
