# Architecture

Current truth of the repository. Designs for work not yet built live in `specs/`.

## Status

| Area | State |
|---|---|
| Gradle foundation (Room, DataStore, WorkManager, serialization, desugaring) | Implemented |
| App shell (`MainActivity` → `AppRoot` placeholder) | Implemented |
| Domain core (XP, levels, ledger, penalties, streaks) | Not started — Phase 1 |
| Product requirements (`specs/003`–`016`) | Specified, not implemented |
| Target architecture (`specs/001`) | Specified, partially in place |

## Current implementation

Single module `:app`, package `com.example.liferpg`, applicationId `com.example.liferpg`.

| Path | Responsibility |
|---|---|
| `com.example.liferpg` | `MainActivity` |
| `com.example.liferpg.ui` | `AppRoot` — placeholder screen, replaced by the dashboard and nav shell |
| `com.example.liferpg.ui.theme` | `LifeRpgTheme`, color schemes, typography |

Startup flow:

```
MainActivity.onCreate
  └─ enableEdgeToEdge()
  └─ LifeRpgTheme { AppRoot() }
```

Build: Gradle 9.1.0, AGP 9.0.1, Kotlin 2.3.20, Compose BOM 2026.03.01, compileSdk/targetSdk 36, minSdk 24, JVM 17, configuration cache on.

Dependencies present but not yet used by code: Room 2.8.5 (KSP), DataStore 1.3.0, WorkManager 2.12.0, kotlinx-serialization-json 1.12.0, Navigation3 1.0.1.

Manifest: no `INTERNET` permission, `allowBackup="false"`. WorkManager's manifest merge adds `WAKE_LOCK`, `ACCESS_NETWORK_STATE`, `RECEIVE_BOOT_COMPLETED`, and `FOREGROUND_SERVICE`; none of them grant network access, and the app opens no sockets.

## Target architecture (Life RPG)

Planned package layout, per `specs/001-architecture.md`:

| Package | Contents |
|---|---|
| `core/` | `TimeProvider`, result types, id generation, formatting |
| `domain/` | pure Kotlin: XP curve, level derivation, gold ledger, streak math, penalty rules, system quest generation |
| `data/` | Room entities and DAOs, repositories, DataStore |
| `ui/<feature>/` | dashboard, quests, shop, achievements, stats, settings |

Rules that will govern the rewrite:

- `domain` has no Android imports and is JVM-testable.
- UI depends on domain and repository interfaces; only ViewModels touch repositories.
- Balances and level are derived from an append-only `quest_events` ledger, never stored as writable fields.
- Room + DataStore; schema exported and committed; no destructive migration fallback.
- Manual `AppContainer` for DI; no framework.
- Offline only: no `INTERNET` permission, no analytics, no accounts.

Data flow:

```
UI event → ViewModel → repository (Room transaction + ledger row) → StateFlow<UiState> → UI
```

## Migration notes

Done in Phase 0:

- Package moved from `com.example.myfastapp` to `com.example.liferpg`; source tree and resources renamed.
- Demo removed: `MainScreen`, `MainScreenViewModel`, `MainScreenUiState`, `DataRepository`, `DefaultDataRepository`, `MainNavigation`, `NavigationKeys`, and both test classes.
- Coil dependencies and the `INTERNET` permission removed.
- `allowBackup="false"`; the backup/data-extraction rule XML files were deleted because nothing is backed up.
- Theme, Navigation3 dependency, version catalog, and Gradle setup carry over.

Still to come:

- `AppRoot` is replaced by the real navigation shell when the quest screens exist.
- The first Room schema file appears in `app/schemas` once a `@Database` class exists (Phase 2).

## Testing

- Current: no tests. The demo tests were deleted with the demo code.
- Target: domain unit tests for XP, gold, penalties, streaks, and deterministic offer generation; Room tests; instrumented flows for create → complete → undo.

## Known constraints

- `compileSdk` must stay 36: AGP 9.0.1 has no `android-37` target.
- App module plugins must be applied, not declared with `apply false`.
- Version catalog aliases must not be prefixes of one another.
- Room 2.8.5 requires KSP2; do not add kapt.
