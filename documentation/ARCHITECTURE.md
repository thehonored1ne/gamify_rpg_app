# Architecture

Current truth of the repository. Designs for work not yet built live in `specs/`.

## Status

| Area | State |
|---|---|
| Demo app (single screen, Coil image) | Implemented |
| Life RPG product requirements (`specs/003`–`016`) | Specified, not implemented |
| Target architecture (`specs/001`) | Specified, not implemented |

## Current implementation

Single module `:app`, package `com.example.myfastapp`.

| Path | Responsibility |
|---|---|
| `com.example.myfastapp` | `MainActivity`, `MainNavigation` (Navigation3 `NavDisplay`), `NavKey` definitions |
| `com.example.myfastapp.data` | `DataRepository` interface, `DefaultDataRepository` (static list) |
| `com.example.myfastapp.ui.main` | `MainScreen`, `MainScreenViewModel`, `MainScreenUiState` |
| `com.example.myfastapp.ui.theme` | `MyFastAppTheme`, color schemes, typography |

Startup flow:

```
MainActivity.onCreate
  └─ enableEdgeToEdge()
  └─ MyFastAppTheme { Surface { MainNavigation() } }
       └─ NavDisplay(entry<Main> { MainScreen(...) })
            └─ MainScreenViewModel(DefaultDataRepository()) → StateFlow<MainScreenUiState>
```

Build: Gradle 9.1.0, AGP 9.0.1, Kotlin 2.3.20, Compose BOM 2026.03.01, compileSdk/targetSdk 36, minSdk 24, JVM 17, configuration cache on.

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

- The demo screen, its ViewModel, and the static repository will be replaced by the quest feature.
- The existing theme, navigation host, version catalog, and Gradle setup carry over.
- Coil and the `INTERNET` permission belong to the demo only; the Life RPG app must not declare `INTERNET`.
- Package will move from `com.example.myfastapp` to `com.example.liferpg` when the rewrite starts.

## Testing

- Current: `MainScreenViewModelTest` (JVM), `MainScreenTest` (instrumented).
- Target: domain unit tests for XP, gold, penalties, streaks, and deterministic offer generation; Room tests; instrumented flows for create → complete → undo.

## Known constraints

- `compileSdk` must stay 36: AGP 9.0.1 has no `android-37` target.
- App module plugins must be applied, not declared with `apply false`.
- Version catalog aliases must not be prefixes of one another.
