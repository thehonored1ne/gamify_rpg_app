# Setup

How to build, install, and debug this project locally. No Android Studio required.

## Prerequisites

| Requirement | Notes |
|---|---|
| JDK 17 | `kotlin { jvmToolchain(17) }` in `app/build.gradle.kts` |
| Android SDK | platform `android-36`, build-tools `36.0.0`, `platform-tools` |
| Device | USB debugging enabled, `adb devices` lists it |
| Editor | Zed (or any editor); no IDE integration is required |

## SDK location

`local.properties` (not committed):

```properties
sdk.dir=C\:\\Android\\sdk
```

## Build and run

```powershell
.\gradlew assembleDebug        # build APK
.\gradlew installDebug         # build + install on the connected device
.\gradlew test                 # JVM unit tests
.\gradlew connectedDebugAndroidTest   # instrumented tests (device required)
.\gradlew clean                # remove build outputs
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`

## Device workflow

```powershell
adb devices                                   # confirm the device is attached
adb install -r app\build\outputs\apk\debug\app-debug.apk
adb shell am start -n com.example.myfastapp/.MainActivity
adb shell am force-stop com.example.myfastapp
adb logcat                                    # crash and runtime logs
adb shell screencap -p /sdcard/shot.png       # screenshot
adb pull /sdcard/shot.png .
adb shell run-as com.example.myfastapp ls -R cache   # inspect app-private cache
```

Device serial used during development: `15187705C5010595`.

## Shell notes

The shell is PowerShell:

- no `&&`, `||`, heredocs, `head`, `tail`, `sed`, `rm -rf`
- use separate lines or `;`, `Select-String` instead of `grep`, `Remove-Item -Recurse -Force` instead of `rm -rf`
- prefer `python -c "..."` for multi-line file edits

## Planned changes (not applied yet)

Per `specs/001`–`016`, the rewrite will:

- rename the package to `com.example.liferpg`
- add Room (with KSP) and DataStore
- drop Coil and the `INTERNET` permission
- add `app/schemas` for exported Room schemas

Until then, the commands above target the demo app.

## Troubleshooting

| Symptom | Cause and fix |
|---|---|
| `Failed to find target with hash string 'android-37'` | AGP 9.0.1 has no `android-37` target; keep `compileSdk = 36` |
| `Unresolved reference 'android'` / `'namespace'` in `app/build.gradle.kts` | plugins declared with `apply false` in the app module; remove `apply false` there |
| `Unresolved reference 'material3'` on `libs.androidx` | alias is `androidx-compose-material3`, accessor is `libs.androidx.compose.material3` |
| `alias 'coil' is not a valid alias` | catalog aliases cannot be prefixes of each other; use `coil-core` + `coil-compose` |
| `module 'io.coil-kt.coil' is not a valid module notation` | use `group` + `name`, or `io.coil-kt:coil` with a colon |
| Configuration cache holds stale state | `.\gradlew --stop`, then delete `.gradle\configuration-cache` |
| Images never appear in the Coil demo | device has no data connection, or `INTERNET` permission missing from the manifest |
| `BUILD SUCCESSFUL` but old UI on device | `installDebug` was not run, or another app instance is in front |

## Related docs

- `documentation/ARCHITECTURE.md` — current and target structure
- `documentation/CHANGELOG.md` — released changes
- `specs/` — requirements for the Life RPG app
- `.agents/rules/` — working rules (build, testing, response, security)
